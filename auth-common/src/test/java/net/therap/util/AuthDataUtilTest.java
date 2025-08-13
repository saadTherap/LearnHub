package net.therap.util;

import net.therap.auth.context.UserRequestCache;
import net.therap.auth.util.AuthDataUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author apurboturjo
 * @since 8/13/25
 */
class AuthDataUtilTest {
    
    private static final Long VALID_USER_ID = 123L;
    private static final Long NON_EXISTENT_USER_ID = 999L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ROLE = "USER";
    
    @BeforeEach
    void setUp() {
        UserRequestCache.remove(VALID_USER_ID);
        UserRequestCache.remove(NON_EXISTENT_USER_ID);
    }
    
    @AfterEach
    void tearDown() {
        UserRequestCache.remove(VALID_USER_ID);
        UserRequestCache.remove(NON_EXISTENT_USER_ID);
    }
    
    @Test
    @DisplayName("Should return UserInfo and remove from cache when userId exists")
    void testGetUserInfo_ValidUserId_ReturnsUserInfoAndRemovesFromCache() {
        UserRequestCache.put(VALID_USER_ID, TEST_EMAIL, TEST_ROLE);
        assertNotNull(UserRequestCache.get(VALID_USER_ID));
        UserRequestCache.UserInfo result = AuthDataUtil.getUserInfo(VALID_USER_ID);
        assertNotNull(result, "UserInfo should not be null for valid userId");
        assertEquals(TEST_EMAIL, result.email(), "Email should match");
        assertEquals(TEST_ROLE, result.role(), "Role should match");
        assertNull(UserRequestCache.get(VALID_USER_ID),
                "UserInfo should be removed from cache after retrieval");
    }
    
    @Test
    @DisplayName("Should return null when userId does not exist in cache")
    void testGetUserInfo_NonExistentUserId_ReturnsNull() {
        UserRequestCache.remove(NON_EXISTENT_USER_ID);
        UserRequestCache.UserInfo result = AuthDataUtil.getUserInfo(NON_EXISTENT_USER_ID);
        assertNull(result, "UserInfo should be null for non-existent userId");
    }
    
    @Test
    @DisplayName("Should return null when userId is null")
    void testGetUserInfo_NullUserId_ReturnsNull() {
        UserRequestCache.UserInfo result = AuthDataUtil.getUserInfo(null);
        assertNull(result, "UserInfo should be null when userId is null");
    }
    
    @Test
    @DisplayName("Should handle multiple consecutive calls correctly")
    void testGetUserInfo_MultipleConsecutiveCalls_HandlesCorrectly() {
        UserRequestCache.put(VALID_USER_ID, TEST_EMAIL, TEST_ROLE);
        UserRequestCache.UserInfo firstResult = AuthDataUtil.getUserInfo(VALID_USER_ID);
        UserRequestCache.UserInfo secondResult = AuthDataUtil.getUserInfo(VALID_USER_ID);
        assertNotNull(firstResult, "First call should return UserInfo");
        assertEquals(TEST_EMAIL, firstResult.email());
        assertEquals(TEST_ROLE, firstResult.role());
        assertNull(secondResult, "Second call should return null as cache was cleared");
    }
    
    @Test
    @DisplayName("Should work correctly with different user data")
    void testGetUserInfo_DifferentUserData_WorksCorrectly() {
        Long userId1 = 100L;
        Long userId2 = 200L;
        String email1 = "user1@test.com";
        String email2 = "user2@test.com";
        String role1 = "ADMIN";
        String role2 = "MANAGER";
        
        UserRequestCache.put(userId1, email1, role1);
        UserRequestCache.put(userId2, email2, role2);
        
        UserRequestCache.UserInfo result1 = AuthDataUtil.getUserInfo(userId1);
        UserRequestCache.UserInfo result2 = AuthDataUtil.getUserInfo(userId2);
        
        assertNotNull(result1);
        assertEquals(email1, result1.email());
        assertEquals(role1, result1.role());
        
        assertNotNull(result2);
        assertEquals(email2, result2.email());
        assertEquals(role2, result2.role());
        
        assertNull(UserRequestCache.get(userId1));
        assertNull(UserRequestCache.get(userId2));
        
        UserRequestCache.remove(userId1);
        UserRequestCache.remove(userId2);
    }
    
    @Test
    @DisplayName("Should not affect other cache entries when retrieving specific user")
    void testGetUserInfo_DoesNotAffectOtherCacheEntries() {
        Long userId1 = 300L;
        Long userId2 = 400L;
        String email1 = "user1@test.com";
        String email2 = "user2@test.com";
        String role1 = "USER";
        String role2 = "ADMIN";
        
        UserRequestCache.put(userId1, email1, role1);
        UserRequestCache.put(userId2, email2, role2);
        
        UserRequestCache.UserInfo result = AuthDataUtil.getUserInfo(userId1);
        
        assertNotNull(result);
        assertEquals(email1, result.email());
        assertEquals(role1, result.role());
        
        assertNull(UserRequestCache.get(userId1), "First user should be removed");
        assertNotNull(UserRequestCache.get(userId2), "Second user should still exist");
        
        UserRequestCache.UserInfo remainingUser = UserRequestCache.get(userId2);
        assertEquals(email2, remainingUser.email());
        assertEquals(role2, remainingUser.role());
        
        UserRequestCache.remove(userId1);
        UserRequestCache.remove(userId2);
    }
}
