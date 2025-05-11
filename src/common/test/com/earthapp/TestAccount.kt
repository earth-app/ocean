package com.earthapp

import com.earthapp.account.Account
import kotlin.test.Test
import kotlin.test.assertTrue

class TestAccount {

    @Test
    fun testNewId() {
        val id = Account.newId()
        assertTrue { id.length == Account.ID_LENGTH }
        assertTrue { id.all { it in Account.ID_CHARACTERS } }
    }

}