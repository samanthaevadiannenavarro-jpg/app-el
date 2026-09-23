package com.example

import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Test

class RoleRoutingTest {

    @Test
    fun testRoleParsing() {
        assertEquals(UserRole.CUSTOMER, UserRole.fromRoleName("customer"))
        assertEquals(UserRole.FLOWER_OWNER, UserRole.fromRoleName("flower_owner"))
        assertEquals(UserRole.ADMIN, UserRole.fromRoleName("admin"))
        // Case insensitivity
        assertEquals(UserRole.CUSTOMER, UserRole.fromRoleName("CUSTOMER"))
        assertEquals(UserRole.FLOWER_OWNER, UserRole.fromRoleName("Flower_Owner"))
        assertEquals(UserRole.ADMIN, UserRole.fromRoleName("Admin"))
        // Unknown defaults safely to customer
        assertEquals(UserRole.CUSTOMER, UserRole.fromRoleName("unknown_role"))
    }
}
