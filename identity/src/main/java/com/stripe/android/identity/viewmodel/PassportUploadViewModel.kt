package com.stripe.android.identity.viewmodel

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stripe.android.core.model.InternalStripeFilePurpose
import com.stripe.android.identity.IdentityVerificationSheetContract
import com.stripe.android.identity.networking.IdentityRepository
import com.stripe.android.identity.utils.ImageChooser
import com.stripe.android.identity.utils.PhotoTaker
import com.stripe.android.identity.utils.resizeUriAndCreateFileToUpload
import kotlinx.coroutines.launch

/**
 * Fragment to upload passport image.
 */
internal class PassportUploadViewModel(
    private val identityRepository: IdentityRepository,
    private val verificationArgs: IdentityVerificationSheetContract.Args
) : ViewModel() {

    /**
     * The passport image is uploaded.
     */
    private val _uploaded = MutableLiveData<Unit>()
    val uploaded: LiveData<Unit> = _uploaded

    private lateinit var photoTaker: PhotoTaker
    private lateinit var imageChooser: ImageChooser

    /**
     * Registers for the [ActivityResultLauncher]s to take photo or pick image, should be called
     * during initialization of an Activity or Fragment.
     */
    internal fun registerActivityResultCaller(activityResultCaller: ActivityResultCaller) {
        photoTaker = PhotoTaker(activityResultCaller)
        imageChooser = ImageChooser(activityResultCaller)
    }

    /**
     * Takes a photo.
     */
    fun takePhoto(
        context: Context,
        onPhotoTaken: (Uri) -> Unit
    ) {
        photoTaker.takePhoto(context, onPhotoTaken)
    }

    /**
     * Choose an image.
     */
    fun chooseImage(
        onImageChosen: (Uri) -> Unit
    ) {
        imageChooser.chooseImage(onImageChosen)
    }

    /**
     * Upload the chosen image, notifies [uploaded] when finished.
     */
    fun uploadImage(
        uri: Uri,
        context: Context
    ) {
        viewModelScope.launch {
            identityRepository.uploadImage(
                verificationId = verificationArgs.verificationSessionId,
                ephemeralKey = verificationArgs.ephemeralKeySecret,
                imageFile = resizeUriAndCreateFileToUpload(
                    context,
                    uri,
                    verificationArgs.verificationSessionId,
                    // TODO(ccen) upload both full frame and non full frame
                    true,
                ),
                // TODO(ccen) pass it over from VerificationPage response
                filePurpose = InternalStripeFilePurpose.IdentityPrivate
            )
            _uploaded.postValue(Unit)
        }
    }

    internal class PassportUploadViewModelFactory(
        private val identityRepository: IdentityRepository,
        private val verificationArgs: IdentityVerificationSheetContract.Args
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PassportUploadViewModel(identityRepository, verificationArgs) as T
        }
    }
}
