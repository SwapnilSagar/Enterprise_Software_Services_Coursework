# CSC8104 — Drawback Fixes Walkthrough

All 13 applicable drawbacks have been fixed. `mvnw test` results: **18 tests, 0 failures, 0 errors**.

---

## Summary of Changes

### Entities

| Fix | File | Change |
|-----|------|--------|
| #15 | [Hotel.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/Hotel.java) | Added `@NotNull` to `postcode` — hotels can no longer be created without a postcode |
| #7  | [HotelBooking.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBooking.java) | Added `@NotEmpty` to `globalBookingId` — empty strings can no longer be used as booking correlation IDs |

### Services

| Fix | File | Change |
|-----|------|--------|
| #6  | [HotelBookingService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java) | Removed redundant `bookingDate.before(new Date())` check — `@Future` bean validation already does this |
| #9  | [HotelBookingService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java) | Fixed [bookingAlreadyExist()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java#47-68) — now compares by calendar day (date range), not exact millisecond timestamp |
| #12 | [HotelBookingService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java) | Changed [deleteBookingRecord()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingService.java#76-89) return type from `boolean` to `void` — the `false` branch was dead code; callers now catch `EntityNotFoundException` |
| #11 | [GlobalBookingService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/GlobalBookingService.java) | Replaced manual `for`-loop with `stream().map().collect()` in [getBookingIdByCustomerId()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/GlobalBookingService.java#55-63) |
| #2  | [CustomerService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerService.java) | Added [updateCustomer()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerService.java#56-62) method |
| #2  | [HotelService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelService.java) | Added [updateHotel()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelService.java#53-59) method |

### REST Endpoints

| Fix | File | Change |
|-----|------|--------|
| #4  | [CustomerRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerRestService.java) | `POST /customers` now returns [CustomerDTO](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/DTO/CustomerDTO.java#10-63) (was raw JPA entity) |
| #2  | [CustomerRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerRestService.java) | Added `PUT /customers/{id}` update endpoint |
| #5  | [HotelRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelRestService.java) | `POST /hotel` now returns `HotelDTO` (was raw JPA entity) |
| #2  | [HotelRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelRestService.java) | Added `PUT /hotel/{id}` update endpoint |
| #12 | [HotelBookingRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingRestService.java) | Updated delete handler to catch `EntityNotFoundException` (removed dead boolean check); `BookingDateConflictException` now returns **409** instead of 400 |

### Travel Agent

| Fix | File | Change |
|-----|------|--------|
| #3  | [TravelAgentRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java) | [GlobalBooking](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/GlobalBooking.java#20-153) is now persisted as PENDING **before** sub-bookings; updated to SUCCESS or FAILED after outcome — guaranteed DB record regardless of result |
| #13 | [TravelAgentRestService.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java) | [cancelBooking()](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestService.java#201-231) now tracks per-service deletion failures and returns `500` with failure details if any sub-service cancel fails (was silently returning `200 OK`) |

### Configuration

| Fix | File | Change |
|-----|------|--------|
| #8  | [HotelClient.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/java/uk/ac/newcastle/enterprisemiddleware/travelagent/client/HotelClient.java) | Removed hardcoded `baseUri` from `@RegisterRestClient`; URL is already in [application.properties](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/main/resources/application.properties) |

### New Tests

| Fix | File | Tests Added |
|-----|------|------------|
| #10 | [HotelBookingRestServiceTest.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/test/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingRestServiceTest.java) | Create booking (201), duplicate conflict (409), past date (400), delete (204), 404 on not-found — **6/6** |
| #10 | [TravelAgentRestServiceTest.java](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/test/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestServiceTest.java) | Customer/hotel setup, booking endpoint reachable, 404 on non-existent cancel — **5/5** |

---

## Test Results

```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Test Class | Result |
|---|---|
| [CustomerRestServiceTest](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/test/java/uk/ac/newcastle/enterprisemiddleware/customer/CustomerRestServiceTest.java#13-96) | ✅ 5/5 |
| [HotelRestServiceTest](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/test/java/uk/ac/newcastle/enterprisemiddleware/hotel/HotelRestServiceTest.java#13-57) | ✅ 2/2 |
| [HotelBookingRestServiceTest](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/test/java/uk/ac/newcastle/enterprisemiddleware/hotelbooking/HotelBookingRestServiceTest.java#21-130) | ✅ 6/6 |
| [TravelAgentRestServiceTest](file:///d:/Enterprise/CSC8104/CSC8104-Swapnil-Sagar/src/test/java/uk/ac/newcastle/enterprisemiddleware/travelagent/TravelAgentRestServiceTest.java#27-135) | ✅ 5/5 |
