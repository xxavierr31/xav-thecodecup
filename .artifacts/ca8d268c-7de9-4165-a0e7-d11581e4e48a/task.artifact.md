# Task List - Profile Picture Upload

- [ ] **Data & Model**
    - [x] Add `imagePath` to `Profile` model
    - [x] Update `ProfilePrefs` to store/retrieve `imagePath`
- [ ] **UI Enhancements**
    - [x] Add camera overlay icon in `fragment_profile.xml`
    - [x] Ensure default fallback to `@drawable/profile` is maintained
- [x] **Logic Implementation**
    - [x] Implement `PickVisualMedia` launcher in `ProfileFragment.kt`
    - [x] Implement internal file storage logic (copying chosen image)
    - [x] Update `ProfileFragment` rendering to use `imagePath`
    - [x] Update `HomeFragment` rendering to use `imagePath`
- [ ] **Verification**
    - [ ] Test photo selection and persistence
    - [ ] Test cross-screen synchronization
    - [ ] Test fallback to default icon
