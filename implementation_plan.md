# CSC8104 — Drawbacks Fix Implementation Plan

Fixing 13 identified drawbacks across entities, services, REST endpoints, Travel Agent orchestration, and tests. Two drawbacks (#1 H2 in-memory DB and #14 Auth) are intentionally skipped as they are either acceptable for coursework or out of scope.

> **Method**: Every change will include an inline comment with the format:
> `// FIX #N — [brief reason]`

---

## Proposed Changes

### Entities

#### [MODIFY] [Hotel.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/Hotel.java)
- **#15** — Add `@NotNull` to `postcode` field so hotels cannot be created without a postcode.

#### [MODIFY] [HotelBooking.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBooking.java)
- **#7** — Add `@NotEmpty` to `globalBookingId` to prevent empty/null correlation IDs being persisted.

---

### Services

#### [MODIFY] [HotelBookingService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java)
- **#6** — Remove redundant `bookingDate.before(new Date())` manual check; `@Future` bean validation handles this already.
- **#9** — Fix [bookingAlreadyExist()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java#48-55) JPQL to compare by calendar date (year/month/day), not exact millisecond timestamp.
- **#12** — Simplify [deleteBookingRecord()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java#63-75) — it throws on not-found, never returns `false`; clean up the dead code.

#### [MODIFY] [GlobalBookingService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/GlobalBookingService.java)
- **#11** — Replace manual `for` loop with `stream().map().collect()` in [getBookingIdByCustomerId()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/GlobalBookingService.java#55-64).

#### [MODIFY] [CustomerService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerService.java)
- **#2** — Add `updateCustomer(Customer)` method that calls `repository.update()`.

#### [MODIFY] [HotelService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelService.java)
- **#2** — Add `updateHotel(Hotel)` method that calls `repository.update()`.

---

### REST Endpoints

#### [MODIFY] [CustomerRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerRestService.java)
- **#4** — Change [createCustomer](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerRestService.java#80-131) response to return `CustomerMapper.toDTO(customer)` instead of the raw entity.
- **#2** — Add `PUT /{id}` endpoint that delegates to `customerService.updateCustomer()`.

#### [MODIFY] [HotelRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelRestService.java)
- **#5** — Change [createHotel](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelRestService.java#80-133) response to return `HotelMapper.toDTO(hotel)` instead of the raw entity.
- **#2** — Add `PUT /{id}` endpoint that delegates to `hotelService.updateHotel()`.

---

### Travel Agent

#### [MODIFY] [TravelAgentRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java)
- **#3** — Persist [GlobalBooking](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/GlobalBooking.java#20-153) with `PENDING` status **before** making sub-bookings, then update to `SUCCESS` or `FAILED`. Guarantees an audit record always exists regardless of outcome.
- **#13** — In [cancelBooking()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java#190-218), track deletion failures per service. If any [safeDelete()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java#221-229) call fails, return a `207 Multi-Status` / `500` with a partial failure report instead of always returning `200 OK`.

---

### Configuration

#### [MODIFY] [HotelClient.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/client/HotelClient.java)
- **#8** — Remove hard-coded `baseUri` from `@RegisterRestClient`. The URL is already correctly set in [application.properties](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/resources/application.properties) via `quarkus.rest-client.hotel-api.url`, so the annotation value is redundant and creates a risk of mismatch.

---

### Tests

#### [NEW] HotelBookingRestServiceTest.java
- Tests: create booking (success), create booking with past date (400), create duplicate booking (409), delete by local ID (204), delete by global ID (204).

#### [NEW] TravelAgentRestServiceTest.java
- Tests: create global booking (success with mocked external clients), cancel booking (success), cancel non-existent booking (404).

> [!IMPORTANT]
> The [TravelAgentRestService](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java#31-249) calls **external** OpenShift-deployed services (Taxi, Taxi2, Hotel). In tests, [application.properties](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/resources/application.properties) already redirects these to `localhost` via `%test.quarkus.rest-client.*` properties, meaning the tests will call the **same running application** (self-referencing). This is intentional and matches the existing test setup.

---

## Verification Plan

### Automated Tests
Run the full test suite after all changes:
```powershell
cd d:\Enterprise\CSC8104\CSC8104-Swapnil-Sagar
.\mvnw test
```
Expected: All existing tests pass + new `HotelBookingRestServiceTest` passes.

> [!NOTE]
> `TravelAgentRestServiceTest` will be written as basic self-contained tests (create customer + hotel, book, cancel) relying on the `%test.*` URL redirects already in [application.properties](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/resources/application.properties).
