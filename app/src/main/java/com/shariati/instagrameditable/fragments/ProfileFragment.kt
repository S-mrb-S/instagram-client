package com.shariati.instagrameditable.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.adapters.HighlightAdapter
import com.shariati.instagrameditable.adapters.PostAdapter
import com.shariati.instagrameditable.data.repository.ApiRepository
import com.shariati.instagrameditable.databinding.BottomSheetHighlightMenuBinding
import com.shariati.instagrameditable.databinding.BottomSheetMenuBinding
import com.shariati.instagrameditable.databinding.BottomSheetPostMenuBinding
import com.shariati.instagrameditable.databinding.DialogAddHighlightBinding
import com.shariati.instagrameditable.databinding.DialogAddPostBinding
import com.shariati.instagrameditable.databinding.DialogRemoveBinding
import com.shariati.instagrameditable.databinding.DialogSetTxtBinding
import com.shariati.instagrameditable.databinding.FragmentProfileBinding
import com.shariati.instagrameditable.models.Highlight
import com.shariati.instagrameditable.models.Post
import com.shariati.instagrameditable.models.PostsResponse
import com.shariati.instagrameditable.utils.getBiography
import com.shariati.instagrameditable.utils.getFollowersCount
import com.shariati.instagrameditable.utils.getFollowsCount
import com.shariati.instagrameditable.utils.getMediaCount
import com.shariati.instagrameditable.utils.getName
import com.shariati.instagrameditable.utils.getProfilePictureUrl
import com.shariati.instagrameditable.utils.getUsername
import com.shariati.instagrameditable.utils.isGetData
import com.shariati.instagrameditable.utils.setDataAccount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), HighlightAdapter.HighlightEvents, PostAdapter.PostEvents {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPref: SharedPreferences
    private val PICK_IMAGE_REQUEST = 1

    //highlight variables
    private var highlightInputStream: InputStream? = null
    private var isHighlightImageChoosed = false
    private lateinit var highlightAdapter: HighlightAdapter
    private var highlightArrayList: ArrayList<Highlight> = arrayListOf()
    private lateinit var highlightBinding: DialogAddHighlightBinding
    private val PICK_IMAGE_REQUEST_HIGHLIGHT = 0

    //post variable
    private var postInputStream: InputStream? = null
    private var ispostImageChoosed = false
    private lateinit var postAdapter: PostAdapter
    private var postArrayList: ArrayList<Post> = arrayListOf()
    private lateinit var postBinding: DialogAddPostBinding
    private val PICK_IMAGE_REQUEST_POST = 2

    @Inject
    lateinit var api: ApiRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        getDataAccountFromLocal()

        isGetData(requireContext(), true)

        getAccount()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressbarLoading.visibility = View.GONE
            binding.profileRecyclerPosts.visibility = View.VISIBLE
        },1000)
        getPost()

        val t = "hello dear @reza how are you?"

        sharedPref = requireContext().getSharedPreferences("profile", Context.MODE_PRIVATE)
        return binding.root
    }

    private fun getDataAccountFromLocal() {
        if (getUsername(requireContext()).isNotEmpty()) {
            Glide.with(requireContext()).load(getProfilePictureUrl(requireContext()))
                .into(binding.profilePageAvatarImage)
            Glide.with(requireContext()).load(getProfilePictureUrl(requireContext()))
                .into(binding.bottomNavigationAvatar)
            binding.profilePageUsername.text = getUsername(requireContext())
            binding.profilePagename.text = getName(requireContext())
            binding.profileBio.text = getBiography(requireContext())
            binding.profileNumberOfPost.text = getMediaCount(requireContext())
            if (getFollowersCount(requireContext()).toInt() > 100000) {
                binding.profileNumberOfFollowers.text =
                    getFollowersCount(requireContext()).substring(0, 3) + "K"
            }
            binding.profileNumberOfFollowing.text = getFollowsCount(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//fill highlightArrayList if there is the highlight in shared preferences
        if (sharedPref.contains("highlightArrayList")) {
            val highlightRes: ArrayList<Highlight> =
                stringToHighlightArrayList(
                    sharedPref.getString("highlightArrayList", null).toString()
                )
            highlightArrayList = highlightRes

        }
//fill postArrayList if there is the post in shared preferences
        if (sharedPref.contains("postArrayList")) {
            val postRes: ArrayList<Post> =
                stringToPostArrayList(
                    sharedPref.getString("postArrayList", null).toString()
                )
            postArrayList = postRes

        }
        //set highlight adapter
        highlightAdapter = HighlightAdapter(this, highlightArrayList, requireContext())
        binding.highlightRecyclerContainer.adapter = highlightAdapter
        binding.highlightRecyclerContainer.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        //get text view text from shared preferences
        // getTxtAlert("userName", binding.profilePageUsername, "page_userName")
        /*getTxtAlert("nOPost", binding.profileNumberOfPost, "22")
        getTxtAlert("nOFollowers", binding.profileNumberOfFollowers, "2,667")
        getTxtAlert("nOFollowing", binding.profileNumberOfFollowing, "76")
        getTxtAlert("pageName", binding.profilePagename, "page name")
        getTxtAlert("bioText", binding.profileBio, "this is an example of bio text")*/
        getTxtAlert("reachedNumber", binding.profileProfessionalDashboardReachedNumber, "1.8k")
        //decode and display image
        decodeAndDisplayImage(binding.profilePageAvatarImage, requireContext(), "profileImage")
        decodeAndDisplayImage(binding.bottomNavigationAvatar, requireContext(), "profileImage")

        /*   binding.profilePageUsername.setOnLongClickListener {
               setTxtAlert("please write your new user name", binding.profilePageUsername, "userName")
               true
           }*/
        /*binding.profileNumberOfPost.setOnLongClickListener {
            setTxtAlert("please write your new posts number", binding.profileNumberOfPost, "nOPost")
            true
        }*/
        /*binding.profileNumberOfFollowers.setOnLongClickListener {
            setTxtAlert(
                "please write your new Followers number",
                binding.profileNumberOfFollowers,
                "nOFollowers"
            )
            true
        }*/
        /* binding.profileNumberOfFollowing.setOnLongClickListener {
             setTxtAlert(
                 "please write your new Following number",
                 binding.profileNumberOfFollowing,
                 "nOFollowing"
             )
             true
         }*/
        /* binding.profilePagename.setOnLongClickListener {
             setTxtAlert("please write your new page name", binding.profilePagename, "pageName")
             true
         }*/
        /*binding.profileBio.setOnLongClickListener {
            setTxtAlert("please write your new Bio", binding.profileBio, "bioText")
            true
        }*/
        binding.profileProfessionalDashboardContainer.setOnLongClickListener {
            setTxtAlert(
                "please write your new reached",
                binding.profileProfessionalDashboardReachedNumber,
                "reachedNumber"
            )
            true
        }
        //set profile image from gallery
        /* binding.profilePageAvatarImage.setOnLongClickListener {
             val galleryIntent =
                 Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
             true
         }*/
        binding.profilePageMenuIcon.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_down
            )
            transaction.replace(R.id.fragment_container, MenuFragment())
            transaction.addToBackStack(null)
            transaction.commit()

          /*  val dialog = BottomSheetDialog(requireContext())
            val bView = BottomSheetMenuBinding.inflate(layoutInflater)
            dialog.setContentView(bView.root)
            dialog.show()
            bView.goToArchive.setOnClickListener {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_up,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_down
                )
                transaction.replace(R.id.fragment_container, ArchiveFragment())
                transaction.addToBackStack(null)
                transaction.commit()
                dialog.dismiss()
            }

            bView.setting.setOnClickListener {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_up,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_down
                )
                transaction.replace(R.id.fragment_container, SettingFragment())
                transaction.addToBackStack(null)
                transaction.commit()
                dialog.dismiss()
            }*/
        }


        //add new highlight
        binding.profileAddHighlight.setOnClickListener {

            highlightBinding = DialogAddHighlightBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(highlightBinding.root)
                .setCancelable(true)
                .create()
            dialog.show()

            highlightBinding.dialogAddHighlightCancelBtn.setOnClickListener {
                isHighlightImageChoosed = false
                dialog.dismiss()
            }
            highlightBinding.itemHighlightImage.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_HIGHLIGHT)
            }
            highlightBinding.dialogAddHighlightConfirmBtn.setOnClickListener {
                if (highlightBinding.dialogAddHighlightName.text!!.isNotEmpty() && isHighlightImageChoosed) {

                    val realImage: Bitmap = BitmapFactory.decodeStream(highlightInputStream)

                    // فشرده‌سازی تصویر به فرمت JPEG و تبدیل به آرایه بایت
                    val baos = ByteArrayOutputStream()
                    realImage.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                    val b: ByteArray = baos.toByteArray()

                    // تبدیل آرایه بایت به رشته Base64
                    val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

                    highlightArrayList.add(
                        0,
                        Highlight(
                            highlightBinding.dialogAddHighlightName.text.toString(),
                            encodedImage
                        )
                    )
                    highlightAdapter.notifyItemInserted(0)
                    sharedPref.edit().putString(
                        "highlightArrayList",
                        highlightArrayListToString(highlightArrayList)
                    ).apply()
                    isHighlightImageChoosed = false
                    dialog.dismiss()

                } else {
                    Toast.makeText(
                        context,
                        "please write highlight name and choose highlight image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        //add new post
        binding.profileAddPost.setOnLongClickListener {

            postBinding = DialogAddPostBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(postBinding.root)
                .setCancelable(true)
                .create()
            dialog.show()

            postBinding.dialogAddPostCancelBtn.setOnClickListener {
                ispostImageChoosed = false
                dialog.dismiss()
            }
            postBinding.itemPostImage.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_POST)
            }
            postBinding.dialogAddPostConfirmBtn.setOnClickListener {
                if (ispostImageChoosed) {
                    val realImage: Bitmap = BitmapFactory.decodeStream(postInputStream)

                    // فشرده‌سازی تصویر به فرمت JPEG و تبدیل به آرایه بایت
                    val baos = ByteArrayOutputStream()
                    realImage.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                    val b: ByteArray = baos.toByteArray()

                    // تبدیل آرایه بایت به رشته Base64
                    val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                    val radio = postBinding.postType.checkedRadioButtonId
                    var postType: Int = 0
                    when (radio) {
                        R.id.post_pin -> {
                            postType = R.drawable.ic_pin
                        }

                        R.id.post_reels -> {
                            postType = R.drawable.ic_reel
                        }

                        R.id.post_slide -> {
                            postType = R.drawable.ic_slide
                        }

                        R.id.post_video -> {
                            postType = R.drawable.ic_video
                        }

                        else -> {
                            postType = 0
                        }
                    }
                    postArrayList.add(
                        0,
                        Post(
                            encodedImage, postType
                        )
                    )
                    postAdapter.notifyItemInserted(0)
                    sharedPref.edit().putString(
                        "postArrayList",
                        postArrayListToString(postArrayList)
                    ).apply()
                    ispostImageChoosed = false
                    dialog.dismiss()

                } else {
                    ispostImageChoosed = false
                    Toast.makeText(
                        context,
                        "please choose post image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            true
        }
    }

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
    //get the profile picture
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
                // اگر عکس انتخاب شده باشد
                val imageUri = data.data
                val inputStream: InputStream? = context?.contentResolver?.openInputStream(imageUri!!)
                binding.profilePageAvatarImage.setImageURI(imageUri)
                binding.bottomNavigationAvatar.setImageURI(imageUri)
                encodeAndSaveImage(inputStream!!, requireContext(), "profileImage")
            }
            //get the highlight picture
            if (requestCode == PICK_IMAGE_REQUEST_HIGHLIGHT && resultCode == Activity.RESULT_OK && data != null) {
                // اگر عکس انتخاب شده باشد
                val imageUri = data.data

                val inputStream: InputStream? = context?.contentResolver?.openInputStream(imageUri!!)
                isHighlightImageChoosed = true
                highlightBinding.itemHighlightImage.setImageURI(imageUri)

                highlightInputStream = inputStream

            }
            //get the post picture
            if (requestCode == PICK_IMAGE_REQUEST_POST && resultCode == Activity.RESULT_OK && data != null) {
                // اگر عکس انتخاب شده باشد
                val imageUri = data.data

                val inputStream: InputStream? = context?.contentResolver?.openInputStream(imageUri!!)
                ispostImageChoosed = true
                postBinding.itemPostImage.setImageURI(imageUri)

                postInputStream = inputStream
            }
        }*/

    fun encodeAndSaveImage(stream: InputStream, context: Context, key: String) {
        // تبدیل تصویر به Bitmap
        val realImage: Bitmap = BitmapFactory.decodeStream(stream)

        // فشرده‌سازی تصویر به فرمت JPEG و تبدیل به آرایه بایت
        val baos = ByteArrayOutputStream()
        realImage.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val b: ByteArray = baos.toByteArray()

        // تبدیل آرایه بایت به رشته Base64
        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

        // ذخیره رشته Base64 در SharedPreferences

        sharedPref.edit().putString(key, encodedImage).apply()
    }

    fun decodeAndDisplayImage(imageConvertResult: ImageView, context: Context, key: String) {

        // دریافت رشته Base64 از SharedPreferences
        val previouslyEncodedImage: String = sharedPref.getString(key, "") ?: ""

        // بررسی آیا رشته معتبر است
        if (previouslyEncodedImage.isNotBlank()) {
            // تبدیل رشته Base64 به آرایه بایت
            val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)

            // تبدیل آرایه بایت به Bitmap
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)

            // نمایش تصویر در ImageView
            imageConvertResult.setImageBitmap(bitmap)
        }
    }

    //this function is for set textView text with an alert dialog
    private fun setTxtAlert(description: String, textView: TextView, sharedKey: String) {
        val view = DialogSetTxtBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context)
            .setView(view.root)
            .setCancelable(true)
            .create()
        dialog.show()

        view.dialogSetTextDescription.text = description
        view.dialogSetTextConfirmBtn.setOnClickListener {
            if (view.dialogSetTextInput.text!!.isNotEmpty()) {
                val newText = view.dialogSetTextInput.text
                textView.text = newText
                dialog.dismiss()
                sharedPref?.edit()?.putString(sharedKey, newText.toString())?.apply()

            } else {
                Toast.makeText(
                    context,
                    "please write a text and then confirm that!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        view.dialogSetTextCancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    //get the text from alert dialog to text view
    private fun getTxtAlert(sharedKey: String, textView: TextView, defaultValue: String) {
        if (sharedPref.contains(sharedKey)) {
            val newText = sharedPref.getString(sharedKey, "")
            textView.text = newText

        } else {
            textView.text = defaultValue
        }
    }

    private fun highlightArrayListToString(highlightArrayList: ArrayList<Highlight>): String {
        return highlightArrayList.joinToString(", ") { "${it.name}: ${it.path}" }
    }

    private fun stringToHighlightArrayList(inputString: String): ArrayList<Highlight> {
        val highlightArrayList = ArrayList<Highlight>()

        // اگر رشته خالی است، لیست را خالی برمی‌گردانیم
        if (inputString.isEmpty()) {
            return highlightArrayList
        }

        val elements = inputString.split(", ")

        for (element in elements) {
            val parts = element.split(":")
            if (parts.size == 2) {
                val title = parts[0].trim()
                val description = parts[1].trim()

                // اضافه کردن یک Highlight جدید به لیست
                highlightArrayList.add(Highlight(title, description))
            } else {
                // اگر فرمت رشته نادرست است، می‌توانید بر اساس نیاز خود عمل کنید (مثلاً خطا را رد کردن)
                // یا از اطلاعات دیگری استفاده کنید
            }
        }
        return highlightArrayList
    }

    private fun postArrayListToString(highlightArrayList: ArrayList<Post>): String {
        return postArrayList.joinToString(", ") { "${it.path}: ${it.icon}" }
    }

    private fun stringToPostArrayList(inputString: String): ArrayList<Post> {
        val postArrayList = ArrayList<Post>()

        // اگر رشته خالی است، لیست را خالی برمی‌گردانیم
        if (inputString.isEmpty()) {
            return postArrayList
        }

        val elements = inputString.split(", ")

        for (element in elements) {
            val parts = element.split(":")
            if (parts.size == 2) {
                val path = parts[0].trim()
                val icon = parts[1].trim()
                // اضافه کردن یک post جدید به لیست
                postArrayList.add(Post(path, icon.toInt()))
            }
        }
        return postArrayList
    }

    override fun highlightEvent(position: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetHighlightMenuBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.show()
        //set remove highlight event
        view.removeHighlight.setOnClickListener {
            dialog.dismiss()
            val view = DialogRemoveBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(context)
                .setView(view.root)
                .setCancelable(true)
                .create()
            dialog.show()
            view.dialogRemoveNo.setOnClickListener {
                dialog.dismiss()
            }
            view.dialogRemoveYes.setOnClickListener {
                highlightArrayList.removeAt(position)
                highlightAdapter.notifyItemRemoved(position)
                if (highlightArrayList.isNotEmpty()) {
                    sharedPref.edit().putString(
                        "highlightArrayList",
                        highlightArrayListToString(highlightArrayList)
                    ).apply()
                } else {
                    sharedPref.edit().remove("highlightArrayList").apply()
                }
                dialog.dismiss()
            }
        }
        //set edit highlight event
        view.editHighlight.setOnClickListener {
            dialog.dismiss()
            highlightBinding = DialogAddHighlightBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(highlightBinding.root)
                .setCancelable(true)
                .create()
            dialog.show()
            //set default highlight image from shared preferences
            val previouslyEncodedImage: String = highlightArrayList[position].path
            if (previouslyEncodedImage.isNotBlank()) {
                val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                highlightBinding.itemHighlightImage.setImageBitmap(bitmap)
                //set default text input value
                highlightBinding.dialogAddHighlightName.setText(highlightArrayList[position].name)

                highlightBinding.dialogAddHighlightCancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                highlightBinding.itemHighlightImage.setOnClickListener {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_HIGHLIGHT)
                }
                highlightBinding.dialogAddHighlightConfirmBtn.setOnClickListener {
                    if (highlightBinding.dialogAddHighlightName.text!!.isNotEmpty() && isHighlightImageChoosed) {

                        val realImage: Bitmap = BitmapFactory.decodeStream(highlightInputStream)

                        // فشرده‌سازی تصویر به فرمت JPEG و تبدیل به آرایه بایت
                        val baos = ByteArrayOutputStream()
                        realImage.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                        val b: ByteArray = baos.toByteArray()

                        // تبدیل آرایه بایت به رشته Base64
                        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

                        highlightArrayList[position].name =
                            highlightBinding.dialogAddHighlightName.text.toString()
                        highlightArrayList[position].path = encodedImage
                        sharedPref.edit().putString(
                            "highlightArrayList",
                            highlightArrayListToString(highlightArrayList)
                        ).apply()
                        isHighlightImageChoosed = false
                        dialog.dismiss()
                        highlightAdapter.notifyItemChanged(position)

                    } else if (highlightBinding.dialogAddHighlightName.text!!.isNotEmpty()) {
                        highlightArrayList[position].name =
                            highlightBinding.dialogAddHighlightName.text.toString()
                        sharedPref.edit().putString(
                            "highlightArrayList",
                            highlightArrayListToString(highlightArrayList)
                        ).apply()
                        isHighlightImageChoosed = false
                        dialog.dismiss()
                        highlightAdapter.notifyItemChanged(position)
                    } else {
                        Toast.makeText(
                            context,
                            "please write highlight name and choose highlight image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun setPostSize(post: ImageView) {
//set post image width and height accept ration : 1*1
        val postWidth = (context?.resources?.displayMetrics?.widthPixels?.div(3)).toString()
        val postHeight = postWidth

        if (postWidth != null) {
            post.layoutParams.width = postWidth.toInt()
            post.layoutParams.height = postHeight.toInt()
        }
    }

    override fun postEvent(position: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetPostMenuBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.show()
        //set remove highlight event
        view.removePost.setOnClickListener {
            dialog.dismiss()
            val view = DialogRemoveBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(context)
                .setView(view.root)
                .setCancelable(true)
                .create()
            dialog.show()
            view.dialogRemoveNo.setOnClickListener {
                dialog.dismiss()
            }
            view.dialogRemoveYes.setOnClickListener {
                postArrayList.removeAt(position)
                postAdapter.notifyItemRemoved(position)
                if (postArrayList.isNotEmpty()) {
                    sharedPref.edit().putString(
                        "postArrayList",
                        postArrayListToString(postArrayList)
                    ).apply()

                } else {
                    sharedPref.edit().remove("postArrayList").apply()
                }
                dialog.dismiss()
            }
        }
        //set edit highlight event
        view.editPost.setOnClickListener {
            dialog.dismiss()
            postBinding = DialogAddPostBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(postBinding.root)
                .setCancelable(true)
                .create()
            dialog.show()
            //set default post image from shared preferences
            val previouslyEncodedImage: String = postArrayList[position].path
            if (previouslyEncodedImage.isNotBlank()) {
                val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                postBinding.itemPostImage.setImageBitmap(bitmap)

                postBinding.dialogAddPostCancelBtn.setOnClickListener {
                    ispostImageChoosed = false
                    dialog.dismiss()
                }
                postBinding.itemPostImage.setOnClickListener {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_POST)
                }
                postBinding.dialogAddPostConfirmBtn.setOnClickListener {
                    if (ispostImageChoosed) {

                        val realImage: Bitmap = BitmapFactory.decodeStream(postInputStream)

                        // فشرده‌سازی تصویر به فرمت JPEG و تبدیل به آرایه بایت
                        val baos = ByteArrayOutputStream()
                        realImage.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                        val b: ByteArray = baos.toByteArray()

                        // تبدیل آرایه بایت به رشته Base64
                        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                        val radio = postBinding.postType.checkedRadioButtonId
                        var postType: Int = 0
                        when (radio) {
                            R.id.post_pin -> {
                                postType = R.drawable.ic_pin
                            }

                            R.id.post_reels -> {
                                postType = R.drawable.ic_reel
                            }

                            R.id.post_slide -> {
                                postType = R.drawable.ic_slide
                            }

                            R.id.post_video -> {
                                postType = R.drawable.ic_video
                            }

                            else -> {
                                postType = 0
                            }
                        }
                        postArrayList[position].path = encodedImage
                        postArrayList[position].icon = postType
                        sharedPref.edit().putString(
                            "postArrayList",
                            postArrayListToString(postArrayList)
                        ).apply()
                        ispostImageChoosed = false
                        dialog.dismiss()
                        postAdapter.notifyItemChanged(position)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "بطفا تصویر را مجدد وارد فرمایید",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getAccount() {
        CoroutineScope(Main).launch {
            val profile = api.getAccount()

            when (profile.code()) {
                200 -> {
                    profile.body()?.let {
                        Glide.with(requireContext()).load(it.profile_picture_url)
                            .into(binding.profilePageAvatarImage)
                        Glide.with(requireContext()).load(it.profile_picture_url)
                            .into(binding.bottomNavigationAvatar)
                        binding.profilePageUsername.text = it.username
                        binding.profilePagename.text = it.name
                        binding.profileBio.text = it.biography
                        binding.profileNumberOfPost.text = it.media_count.toString()
                        binding.profileNumberOfFollowers.text = formatNumberToK(it.followers_count)

                        binding.profileNumberOfFollowing.text = it.follows_count.toString()

                        setDataAccount(
                            requireContext(),
                            it.username,
                            it.name,
                            it.biography,
                            it.media_count.toString(),
                            it.profile_picture_url,
                            it.followers_count.toString(),
                            it.follows_count.toString()
                        )

                        getDataAccountFromLocal()
                    }
                }
            }
        }
    }

    fun formatNumberToK(number: Int): String {
        val numberInThousands = number / 1000.0
        return String.format("%.1fK", numberInThousands)
    }

    private fun getPost() {
        CoroutineScope(Main).launch {
            val posts = api.getPost()
            when (posts.code()) {
                200 -> {
                    posts.body()?.let {
                        val postList = ArrayList<PostsResponse.Data>()

                        for (post in it.data) {
                            postList.add(post)
                        }

                        postAdapter = PostAdapter(postList, this@ProfileFragment, requireContext())
                        binding.profileRecyclerPosts.adapter = postAdapter
                        binding.profileRecyclerPosts.layoutManager =
                            GridLayoutManager(requireContext(), 3)
                    }
                }
            }
        }
    }
}
