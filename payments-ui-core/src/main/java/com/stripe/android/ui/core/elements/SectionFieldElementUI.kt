package com.stripe.android.ui.core.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SectionFieldElementUI(
    enabled: Boolean,
    field: SectionFieldElement,
    hiddenIdentifiers: List<IdentifierSpec>? = null,
    lastTextFieldIdentifier: IdentifierSpec?,
    modifier: Modifier = Modifier,
) {
    if (hiddenIdentifiers?.contains(field.identifier) == false) {
        when (val controller = field.sectionFieldErrorController()) {
            is TextFieldController -> {
                TextField(
                    textFieldController = controller,
                    enabled = enabled,
                    modifier = modifier,
                    isImeDone = lastTextFieldIdentifier == field.identifier
                )
            }
            is DropdownFieldController -> {
                DropDown(
                    controller,
                    enabled
                )
            }
            is AddressController -> {
                AddressElementUI(
                    enabled,
                    controller,
                    hiddenIdentifiers,
                    lastTextFieldIdentifier
                )
            }
            is RowController -> {
                RowElementUI(
                    enabled,
                    controller,
                    hiddenIdentifiers,
                    lastTextFieldIdentifier
                )
            }
            is CardDetailsController -> {
                CardDetailsElementUI(
                    enabled,
                    controller,
                    hiddenIdentifiers,
                    lastTextFieldIdentifier
                )
            }
        }
    }
}
