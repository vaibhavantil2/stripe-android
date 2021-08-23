package com.stripe.android.paymentsheet.forms

import com.google.common.truth.Truth.assertThat
import com.stripe.android.paymentsheet.FormElement
import com.stripe.android.paymentsheet.SectionFieldElement
import com.stripe.android.paymentsheet.elements.EmailConfig
import com.stripe.android.paymentsheet.elements.SectionController
import com.stripe.android.paymentsheet.elements.TextFieldController
import com.stripe.android.paymentsheet.specifications.IdentifierSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TransformElementToFormViewValueFlowTest {

    private val emailController = TextFieldController(EmailConfig())
    private val emailSection = FormElement.SectionElement(
        identifier = IdentifierSpec("emailSection"),
        SectionFieldElement.Email(
            IdentifierSpec("email"),
            emailController
        ),
        SectionController(emailController.label, listOf(emailController))
    )

    private val hiddenIdentifersFlow = MutableStateFlow<List<IdentifierSpec>>(emptyList())

    private val fieldFlow = MutableStateFlow(
        mapOf(
            IdentifierSpec("country") to FormFieldEntry("US", true),
            IdentifierSpec("email") to FormFieldEntry("email@email.com", false),
        )
    )

    private val transformElementToFormFieldValueFlow = TransformElementToFormFieldValueFlow(
        fieldFlow,
        hiddenIdentifersFlow,
        showingMandate = MutableStateFlow(true),
        saveForFutureUse = MutableStateFlow(false)
    )

    @Test
    fun `With only some complete controllers and no hidden values the flow value is null`() {
        runBlockingTest {
            assertThat(transformElementToFormFieldValueFlow.transformFlow().first()).isNull()
        }
    }

    @Test
    fun `If all controllers are complete and no hidden values the flow value has all values`() {
        runBlockingTest {
            fieldFlow.value =
                mapOf(
                    IdentifierSpec("country") to FormFieldEntry("US", true),
                    IdentifierSpec("email") to FormFieldEntry("email@email.com", true),
                )

            val formFieldValue = transformElementToFormFieldValueFlow.transformFlow().first()

            assertThat(formFieldValue).isNotNull()
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(IdentifierSpec("email"))
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(IdentifierSpec("country"))
        }
    }

    @Test
    fun `If an hidden field is incomplete field pairs have the non-hidden values`() {
        runBlockingTest {
            hiddenIdentifersFlow.value = listOf(IdentifierSpec("email"))

            val formFieldValues = transformElementToFormFieldValueFlow.transformFlow()

            val formFieldValue = formFieldValues.first()
            assertThat(formFieldValue).isNotNull()
            assertThat(formFieldValue?.fieldValuePairs)
                .doesNotContainKey(IdentifierSpec("email"))
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(IdentifierSpec("country"))
        }
    }

    @Test
    fun `If an hidden field is complete field pairs contain only the non-hidden values`() {
        runBlockingTest {
            fieldFlow.value =
                mapOf(
                    IdentifierSpec("country") to FormFieldEntry("US", true),
                    IdentifierSpec("email") to FormFieldEntry("email@email.com", true),
                )

            hiddenIdentifersFlow.value = listOf(emailSection.fields[0].identifier)

            val formFieldValue = transformElementToFormFieldValueFlow.transformFlow().first()

            assertThat(formFieldValue).isNotNull()
            assertThat(formFieldValue?.fieldValuePairs)
                .doesNotContainKey(IdentifierSpec("email"))
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(IdentifierSpec("country"))
        }
    }
}
