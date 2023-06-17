package services.storemanagement;

import com.tradr.springboot.view.storeclasses.*;
import com.tradr.springboot.view.userclasses.UserAuthKey;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import services.interfaces.CheckProfanity;
import services.registration.UserManagementService;
import services.resourceprocessor.ProfanityProcessorService;
import services.utils.DatabaseVerification;
import services.utils.HashUtils;
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

@Service
public class StoreManagementService {

	public StoreEnums insertStore(
		Store store,
		UserManagementService userManagementService
	) {
		CheckProfanity check = (String stringToCheck) ->
			ProfanityProcessorService.inspectString(
				stringToCheck.toLowerCase(Locale.getDefault())
			);

		if (
			check.call(store.getStoreTitle()) ||
			check.call(store.getStoreDescription()) ||
			check.call(store.getPostcode()) ||
			check.call(store.getAddressLine1()) ||
			check.call(store.getAddressLine2()) ||
			check.call(store.getAddressLine3())
		) {
			return StoreEnums.STORE_CREATION_FAILED_PROFANITY;
		}

		// Get the associated authkey and assign it to an authkey object.
		// @TODO Do we need this to be an actual object? Probably. It may need to be expanded upon later.
		UserAuthKey authKey = new UserAuthKey();
		authKey.setAuthKey(store.getAuthKey());

		// Check to see whether the authkey is in date and return an invalid user if it isn't.
		boolean isUserValid = userManagementService.isAuthKeyValid(authKey);

		if (!isUserValid) {
			return StoreEnums.INVALID_USER;
		}
		// Check to see if the user actually has an extant store, users may only have one store (As of 09/06/2023 this could be expanded to include more.)
		String userHasStore = userManagementService.getUserOwnedStoreUUID(
			authKey.getAuthKey()
		);

		if (!Objects.equals(userHasStore, "null")) {
			return StoreEnums.STORE_CREATION_FAILED_STORE_EXISTS;
		}

		// Validate the UUID? This seems kind of redundant right now because we're doing an auth call above but this won't hurt to keep?
		// This was probably how we intended to initially build the validation for the user's ability to add a store. UUIDs are now a 'soft secret'?
		// @TODO Revisit whether we need this.
		CompletableFuture<Boolean> validateUUID = CompletableFuture.supplyAsync(() -> {
				try {
					return isUUIDValid(store.getParentUUID());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		);

		if (!validateUUID.join()) {
			return StoreEnums.INVALID_UUID;
		}

		// Insert the store into the stores table.
		CompletableFuture<StoreEnums> insertStoreCompletableFuture = CompletableFuture.supplyAsync(() -> {
				String craftTagsAsString = String.join(
					",",
					store.getCraftTags()
				);
				String storeOwnUUD = HashUtils.generateUUID();
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"INSERT INTO stores (" +
						"storeTitle, " +
						"storeDescription, " +
						"canMessage, " +
						"isPrivate, " +
						"storeTheme, " +
						"craftTags, " +
						"addressLine1, " +
						"addressLine2, " +
						"addressLine3, " +
						"postcode, " +
						"parentUUID, " +
						"ownUUID, " +
						"publicStoreId, " +
						"storeAvatar, " +
						"storeBanner) VALUES " +
						"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
					);
				) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
						Base64.getDecoder().decode(store.getStoreBanner())
					);
					statement.setString(1, store.getStoreTitle());
					statement.setString(2, store.getStoreDescription());
					statement.setBoolean(3, store.isCanMessage());
					statement.setBoolean(4, store.isPrivate());
					statement.setString(5, store.getStoreTheme());
					statement.setString(6, craftTagsAsString);
					statement.setString(7, store.getAddressLine1());
					statement.setString(8, store.getAddressLine2());
					statement.setString(9, store.getAddressLine3());
					statement.setString(10, store.getPostcode());
					statement.setString(11, store.getParentUUID());
					statement.setString(12, storeOwnUUD);
					statement.setString(
						13,
						HashUtils.generatePublicId(store.getStoreTitle())
					);

					if (store.getStoreAvatar() != null) {
						statement.setBinaryStream(
							14,
							inputStream,
							inputStream.available()
						);
					} else {
						statement.setNull(14, Types.BLOB);
					}

					if (store.getStoreBanner() != null) {
						statement.setBinaryStream(
							15,
							inputStream,
							inputStream.available()
						);
					} else {
						statement.setNull(15, Types.BLOB);
					}

					int rowsInserted = statement.executeUpdate();

					// Update the user's owned store UUID, over-writing the existing one.
					boolean userOwnedStoresUUIDUpdated = userManagementService.updateUserOwnedStoreUUID(
						storeOwnUUD,
						store.getParentUUID(),
						false
					);

					LoggingUtils.log(rowsInserted);

					return (rowsInserted > 0 && userOwnedStoresUUIDUpdated)
						? StoreEnums.STORE_INSERTED
						: StoreEnums.STORE_INSERTION_FAILED;
				} catch (SQLException e) {
					return StoreEnums.STORE_INSERTION_FAILED;
				}
			}
		);

		return insertStoreCompletableFuture.join();
	}

	public StoreEnums prepareStoreItemsForInsertion(
		StoreItem storeItem,
		String parentUUID
	) {
		CheckProfanity check = (String stringToCheck) ->
			ProfanityProcessorService.inspectString(
				stringToCheck.toLowerCase(Locale.getDefault())
			);

		if (
			check.call(storeItem.getStoreItemDescription()) ||
			check.call(storeItem.getStoreItemName())
		) {
			return StoreEnums.ITEM_INSERTION_FAILED_PROFANITY;
		}
		// Place a single store item into the storeItems table.
		CompletableFuture<StoreEnums> prepareStoreItemsForInsertionCompletableFuture = CompletableFuture.supplyAsync(() -> {
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"INSERT INTO storeitems (storeItemName, parentUUID, storeItemImage, storeItemDescription, storeItemPrice, storeItemPublicId) VALUES " +
						"(?, ?, ?, ?, ?, ?)"
					);
				) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
						Base64
							.getDecoder()
							.decode(storeItem.getStoreItemImage())
					);
					statement.setString(1, storeItem.getStoreItemName());
					statement.setString(2, parentUUID);
					if (storeItem.getStoreItemImage() != null) {
						statement.setBinaryStream(
							3,
							inputStream,
							inputStream.available()
						);
					} else {
						statement.setNull(3, Types.BLOB);
					}
					statement.setString(4, storeItem.getStoreItemDescription());
					statement.setString(5, storeItem.getStoreItemPrice());
					statement.setString(
						6,
						HashUtils.generatePublicId(storeItem.getStoreItemName())
					);
					int rowsInserted = statement.executeUpdate();
					return (rowsInserted > 0)
						? StoreEnums.ITEM_INSERTED
						: StoreEnums.ITEM_INSERTION_FAILED;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		);

		return prepareStoreItemsForInsertionCompletableFuture.join();
	}

	// @TODO NEEDS PAGINATION?
	public StoreSummaryResponse getStoresListSummaryFromDatabase() {
		// Get a summarised list of stores from the database.
		// @TODO are we sure we want to send back the actual storeUUID in this response?
		CompletableFuture<StoreSummaryResponse> storeSummaryResponseCompletableFuture = CompletableFuture.supplyAsync(() -> {
				StoreSummaryResponse response = new StoreSummaryResponse();
				response.setStoreSummaryQueryStatus(
					StoreEnums.STORE_LIST_EMPTY
				);
				List<StoreSummary> storeSummaries = new ArrayList<StoreSummary>();
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"SELECT storeTitle, storeDescription, storeTheme, ownUUID, storeBanner, publicStoreId FROM stores"
					);
					ResultSet rs = statement.executeQuery();
				) {
					if (!rs.next()) {
						return response;
					}

					do {
						response.setStoreSummaryQueryStatus(
							StoreEnums.STORE_LIST_POPULATED
						);
						StoreSummary storeSummary = new StoreSummary();
						String storeTitle = rs.getString("storeTitle");
						String storeDescription = rs.getString(
							"storeDescription"
						);
						String ownUUID = rs.getString("ownUUID");
						String storeTheme = rs.getString("storeTheme");
						String publicStoreId = rs.getString("publicStoreId");

						storeSummary.setStoreTitle(storeTitle);
						storeSummary.setStoreDescription(storeDescription);
						storeSummary.setOwnUUID(ownUUID);
						storeSummary.setStoreTheme(storeTheme);
						storeSummary.setPublicStoreId(publicStoreId);
						storeSummaries.add(storeSummary);
					} while (rs.next());

					response.setStores(storeSummaries);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}

				return response;
			}
		);

		return storeSummaryResponseCompletableFuture.join();
	}

	public StoreResponse getIndividualStore(String storeId) {
		// Get the individual store's details using the public store ID, i.e. lucys-bakes-95739271
		// @TODO This response sends back the Store object as a part of StoreResponse, this isn't a safe object to send back and it should be refactored.
		CompletableFuture<StoreResponse> getIndividualStoreResponseCompletableFuture = CompletableFuture.supplyAsync(() -> {
				StoreResponse response = new StoreResponse();
				response.setStoreQueryResponseStatus(StoreEnums.STORE_EMPTY);

				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"SELECT * FROM stores WHERE publicStoreId = ?"
					);
				) {
					statement.setString(1, storeId);
					ResultSet rs = statement.executeQuery();

					if (!rs.next()) {
						return response;
					}

					Store store = new Store();

					response.setStoreQueryResponseStatus(
						StoreEnums.STORE_POPULATED
					);
					String[] craftingTags = rs
						.getString("craftTags")
						.split(",");

					List<String> craftingTagsAsList = new ArrayList<>(
						Arrays.asList(craftingTags)
					);
					List<StoreItem> storeItems = getStoreItemsForIndividualStore(
						rs.getString("ownUUID")
					);

					store.setStoreTitle(rs.getString("storeTitle"));
					store.setStoreDescription(rs.getString("storeDescription"));
					store.setCanMessage(rs.getBoolean("canMessage"));
					store.setPrivate(rs.getBoolean("isPrivate"));
					store.setStoreTheme(rs.getString("storeTheme"));
					store.setCraftTags(craftingTagsAsList);
					store.setAddressLine1(rs.getString("addressLine1"));
					store.setAddressLine2(rs.getString("addressLine2"));
					store.setAddressLine3(rs.getString("addressLine3"));
					store.setPostcode(rs.getString("postcode"));
					store.setOwnUUID(rs.getString("ownUUID"));
					store.setPublicStoreId(rs.getString("publicStoreID"));
					store.setStoreItems(storeItems);

					response.setStore(store);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}

				return response;
			}
		);

		return getIndividualStoreResponseCompletableFuture.join();
	}

	private List<StoreItem> getStoreItemsForIndividualStore(
		String storeItemsParentUUID
	) throws SQLException {
		List<StoreItem> storeItemsFinal = new ArrayList<StoreItem>();

		// Using the store's actual UUID, get all items from the storeItems table which match the UUID.
		CompletableFuture<List<StoreItem>> getStoreItemsForIndividualStoreCompletableFuture = CompletableFuture.supplyAsync(() -> {
				List<StoreItem> storeItems = new ArrayList<StoreItem>();
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"SELECT * FROM storeItems WHERE parentUUID = ?"
					);
				) {
					statement.setString(1, storeItemsParentUUID);
					ResultSet rs = statement.executeQuery();

					if (!rs.next()) {
						return storeItems;
					}

					do {
						StoreItem storeItem = new StoreItem();
						storeItem.setStoreItemName(
							rs.getString("storeItemName")
						);
						storeItem.setStoreParentUUID(
							rs.getString("parentUUID")
						);
						storeItem.setStoreItemDescription(
							rs.getString("storeItemDescription")
						);
						storeItem.setStoreItemPrice(
							rs.getString("storeItemPrice")
						);
						storeItem.setStoreItemPublicId(
							rs.getString("storeItemPublicId")
						);
						storeItems.add(storeItem);
					} while (rs.next());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				return storeItems;
			}
		);

		storeItemsFinal =
			getStoreItemsForIndividualStoreCompletableFuture.join();

		return storeItemsFinal;
	}

	public StoreEnums deleteItem(
		String storeItemPublicId,
		String authKey,
		UserManagementService userManagementService
	) {
		// Get the user's owned store UUID by auth key.
		String userChildStoreUUID = userManagementService.getUserOwnedStoreUUID(
			authKey
		);
		// If the user has no child store then they can't delete items from a store.
		if (Objects.equals(userChildStoreUUID, "")) {
			return StoreEnums.ITEM_DELETION_FAILED;
		}
		// Delete items where the public store item ID (i.e. lucys-bakes-95739271) and the 'parent UUID' which is the user's 'child store UUID' (which is basically the store's UUID)
		CompletableFuture<StoreEnums> deleteItemCompletableFuture = CompletableFuture.supplyAsync(() -> {
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"DELETE FROM storeItems WHERE storeItemPublicId = ? AND parentUUID = ?;"
					);
				) {
					statement.setString(1, storeItemPublicId);
					statement.setString(2, userChildStoreUUID);
					int rowsDeleted = statement.executeUpdate();
					return (rowsDeleted > 0)
						? StoreEnums.ITEM_DELETED
						: StoreEnums.ITEM_DELETION_FAILED;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		);
		return deleteItemCompletableFuture.join();
	}

	public StoreEnums deleteStore(
		UserAuthKey authKey,
		String userChildStoreUUID
	) {
		// Delete any relevant items that are a part of the user's owned store.
		CompletableFuture<StoreEnums> deleteStoreItemsFromStore = CompletableFuture.supplyAsync(() -> {
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"DELETE FROM storeItems WHERE parentUUID = ?"
					);
				) {
					statement.setString(1, userChildStoreUUID);

					int itemsDeleted = statement.executeUpdate();

					return (itemsDeleted > 0)
						? StoreEnums.ITEMS_DELETED
						: StoreEnums.ITEMS_DELETION_FAILED;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		);
		// Might be used later?
		StoreEnums itemsFromStoreDeleted = deleteStoreItemsFromStore.join();
		// Delete the store owned by the user.
		CompletableFuture<StoreEnums> deleteIndividualStoreCompletableFuture = CompletableFuture.supplyAsync(() -> {
				try (
					Connection conn = DatabaseVerification.getConnection();
					PreparedStatement statement = conn.prepareStatement(
						"DELETE FROM stores WHERE ownUUID = ?"
					);
				) {
					statement.setString(1, userChildStoreUUID);
					int rowsDeleted = statement.executeUpdate();
					return (rowsDeleted > 0)
						? StoreEnums.STORE_DELETION_SUCCESSFUL
						: StoreEnums.STORE_DELETION_FAILED;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		);
		return deleteIndividualStoreCompletableFuture.join();
	}

	public boolean isUUIDValid(String uuid) throws Exception {
		try (
			Connection conn = DatabaseVerification.getConnection();
			PreparedStatement statement = conn.prepareStatement(
				"SELECT * FROM users WHERE uuid = '" + uuid + "'"
			);
			ResultSet rs = statement.executeQuery()
		) {
			if (!rs.next()) {
				throw new Exception("No such UUID key");
			}
			return true;
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}
}
