# ALPHA FEATURES LIST (PHASE 1)

## User Management

-   [x] User Registration endpoint.
-   [x] User Login endpoint.
-   [x] Auth key validator endpoint
-   [x] Delete user endpoint.
-   [x] Update user details endpoint.
-   [ ] Get user images endpoint.

## Store Management

-   [x] Create new store endpoint.
-   [x] Add item to existing store endpoint.
-   [x] Delete existing store endpoint.
-   [x] Delete existing item endpoint.
-   [x] Get store items images endpoint.
-   [x] Get store images endpoint.

## Store View

-   [x] View Store endpoint.
-   [ ] (Optional) View individual item endpoint.

## Search

-   [x] Summarised list endpoint.
-   [x] Search endpoint.

## Moderation

-   [ ] Admin disable store endpoint.
-   [ ] Admin disable user endpoint.
-   [ ] Admin suspend store endpoint.
-   [ ] User report store endpoint.
-   [ ] Admin list of stores to be reviewed endpoint.

## OUTSTANDING TASKS

-   [ ] Remove unused fields from store and user endpoints.
-   [x] Implement language filter in Store Name and User Name, and Item Description.
-   [x] Explore postcode based search.
-   [ ] Replace username and storename with @ in the beginning.

# KNOWN TODO LOCATIONS:

`insertStore()`,
`validateAndUpdateAuthKey()`,
`getStoresListSummaryFromDatabase()`
`getIndividualStore()`
