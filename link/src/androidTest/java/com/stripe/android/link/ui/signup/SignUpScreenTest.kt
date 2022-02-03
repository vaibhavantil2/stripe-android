package com.stripe.android.link.ui.signup

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stripe.android.link.LinkActivity
import com.stripe.android.link.R
import com.stripe.android.link.ui.theme.DefaultLinkTheme
import com.stripe.android.ui.core.elements.EmailSpec
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalAnimationApi
@RunWith(AndroidJUnit4::class)
internal class SignUpScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LinkActivity>()

    @Test
    fun status_inputting_email_shows_only_email_field() {
        setContent(SignUpStatus.InputtingEmail)

        onEmailField().assertExists()
        onEmailField().assertIsEnabled()
        onProgressIndicator().assertDoesNotExist()
        onPhoneField().assertDoesNotExist()
        onSignUpButton().assertDoesNotExist()
    }

    @Test
    fun status_verifying_email_is_disabled() {
        setContent(SignUpStatus.VerifyingEmail)

        onEmailField().assertExists()
        onEmailField().assertIsNotEnabled()
        onProgressIndicator().assertExists()
        onPhoneField().assertDoesNotExist()
        onSignUpButton().assertDoesNotExist()
    }

    @Test
    fun status_inputting_phone_shows_all_fields() {
        setContent(SignUpStatus.InputtingPhone)

        onEmailField().assertExists()
        onEmailField().assertIsEnabled()
        onProgressIndicator().assertDoesNotExist()
        onPhoneField().assertExists()
        onPhoneField().assertIsEnabled()
        onSignUpButton().assertExists()
        onSignUpButton().assertIsEnabled()
    }

    private fun setContent(signUpStatus: SignUpStatus) =
        composeTestRule.setContent {
            DefaultLinkTheme {
                SignUpBody(
                    merchantName = "Example, Inc.",
                    emailElement = EmailSpec.transform(""),
                    signUpStatus = signUpStatus
                )
            }
        }

    private fun onEmailField() = composeTestRule.onNodeWithText("Email")
    private fun onProgressIndicator() = composeTestRule.onNodeWithTag("CircularProgressIndicator")
    private fun onPhoneField() = composeTestRule.onNodeWithText("Mobile Number")
    private fun onSignUpButton() = composeTestRule.onNodeWithText(getString(R.string.sign_up))

    private fun getString(resId: Int) = composeTestRule.activity.getString(resId)
}
