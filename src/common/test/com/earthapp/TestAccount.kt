package com.earthapp

import com.earthapp.account.Account
import com.earthapp.util.ID_CHARACTERS
import com.earthapp.util.ID_LENGTH
import kotlin.test.Test
import kotlin.test.assertTrue

class TestAccount {

    @Test
    fun testNewId() {
        val id = Account.newId()
        assertTrue { id.length == ID_LENGTH }
        assertTrue { id.all { it in ID_CHARACTERS } }
    }

}