package com.shariati.instagrameditable.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.data.repository.ApiRepository
import com.shariati.instagrameditable.databinding.FragmentStoryBinding
import com.shariati.instagrameditable.models.StoriesResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding
    private lateinit var bottomSheet: BottomSheetDialog
    lateinit var profileShared: SharedPreferences

    private lateinit var storyArrayList: StoriesResponse.Data
    private lateinit var dataArrayList: StoriesResponse.Data
    private lateinit var storyArray: ArrayList<StoriesResponse.Data>
    private var storyPosition = 0

    var p = 0

    private var image = ""
    private var size = 0


    @Inject
    lateinit var api: ApiRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(layoutInflater)
        profileShared = requireContext().getSharedPreferences("profile", Context.MODE_PRIVATE)

        getAccount()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mpbMain.pause()

        val bundle = arguments

        if (bundle != null) {
            storyArrayList = bundle.getParcelable<StoriesResponse.Data>("story")!!
            storyPosition = bundle.getInt("storyPosition")
            image = bundle.getString("imageStory").toString()
            size = bundle.getInt("storySize")

            Glide.with(requireContext())
                .load(image)
                .into(binding.storyImage)

            val sharedPref = requireContext().getSharedPreferences("story", Context.MODE_PRIVATE)

            if (sharedPref.contains("storyArrayList")) {
                val storyRes: ArrayList<StoriesResponse.Data> =
                    stringToStoryArrayList(
                        sharedPref.getString("storyArrayList", null).toString()
                    )
                storyArray = ArrayList(storyRes.distinctBy { it.id })
            }

            p = storyPosition

            binding.mpbMain.setProgressStepsCount(size)//7
            binding.mpbMain.start(((size - 1) - storyPosition))
            binding.mpbMain.pause()
            val d = storyArrayList.timestamp.substring(0, 8)

            val date = LocalDate.parse(d, DateTimeFormatter.BASIC_ISO_DATE)

            val formattedDate =
                date.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH))

            binding.storyDate.text =
                formattedDate + "  " + storyArrayList.timestamp.substring(
                    8,
                    10
                ) + ":" + storyArrayList.timestamp.substring(10, 12)

            val profileImage = profileShared.getString("profileImage", "")
            val previouslyEncodedImage: String = profileImage.toString()

            CoroutineScope(Main).launch {
                delay(3800L)
                binding.mpbMain.pause()
            }

            //set random story viewer avatar
            val randomNum = generateRandomNumbers(0, 24)
            val profileViewerList = listOf(
                binding.storyProfileIconViewer1Image,
                binding.storyProfileIconViewer2Image,
                binding.storyProfileIconViewer3Image
            )
            var i = 0
            profileViewerList.forEach {
                val profImage = when (randomNum[i]) {
                    0 -> R.drawable.prof1
                    1 -> R.drawable.prof2
                    2 -> R.drawable.prof3
                    3 -> R.drawable.prof4
                    4 -> R.drawable.prof5
                    5 -> R.drawable.prof6
                    6 -> R.drawable.prof7
                    7 -> R.drawable.prof8
                    8 -> R.drawable.p1
                    9 -> R.drawable.p2
                    10 -> R.drawable.p3
                    11 -> R.drawable.p4
                    12 -> R.drawable.p5
                    13 -> R.drawable.p6
                    14 -> R.drawable.p7
                    15 -> R.drawable.p8
                    16 -> R.drawable.p9
                    17 -> R.drawable.p10
                    18 -> R.drawable.p11
                    19 -> R.drawable.p12
                    20 -> R.drawable.p13
                    21 -> R.drawable.p14
                    22 -> R.drawable.p15
                    23 -> R.drawable.p16
                    24 -> R.drawable.p17

                    else -> R.drawable.img_profile_avatar
                }
                it.setImageResource(profImage)
                i++
            }

//set story image width and height accept ration : 9*16
            val storyWidth = context?.resources?.displayMetrics?.widthPixels.toString()
            val storyHeight = (storyWidth.toInt() / 9) * 16

            if (storyWidth != null) {
                binding.storyImage.layoutParams.width = storyWidth.toInt()
            }
            if (storyHeight != null) {
                binding.storyImage.layoutParams.height = storyHeight
            }
            //go to insight fragment
            binding.goToInsight.setOnClickListener {
                val insightFrag = InsightFragment()
                val bundle = Bundle()
                bundle.putParcelable("story", storyArrayList)
                bundle.putInt("storyPosition", storyPosition)
                bundle.putString("imageStory", image)
                insightFrag.arguments = bundle
                val transaction = parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.scale_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.scale_out
                    )
                    .replace(R.id.fragment_container, insightFrag)
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.goToInsight.setOnClickListener {

            val a = arguments


            val insightFrag = InsightFragment()
            val bundle = Bundle()
            bundle.putParcelable("story", a?.getParcelable<StoriesResponse.Data>("story")!!)
            bundle.putInt("storyPosition", ((size - 1) - storyPosition))
            bundle.putString("imageStory", a.getString("imageStory").toString())
            bundle.putString("storySize", a.getString("storySize").toString())
            insightFrag.arguments = bundle

            val transaction = parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.scale_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.scale_out
                )
                .replace(R.id.fragment_container, insightFrag)
                .addToBackStack(null)
                .commit()
        }
    }

    fun generateRandomNumbers(min: Int, max: Int): List<Int> {
        val numbers = mutableListOf<Int>()
        while (numbers.size < 3) {
            val number = Random.nextInt(min, max + 1)
            if (!numbers.contains(number)) {
                numbers.add(number)
            }
        }
        return numbers
    }

    private fun getAccount() {
        CoroutineScope(Main).launch {
            val profile = api.getAccount()
            when (profile.code()) {
                200 -> {
                    profile.body()?.let {
                        Glide.with(requireContext()).load(it.profile_picture_url)
                            .into(binding.storyProfileIcon)
                    }
                }
            }
        }
    }

    private fun stringToStoryArrayList(inputString: String): ArrayList<StoriesResponse.Data> {
        val storyArrayList = ArrayList<StoriesResponse.Data>()

        // اگر رشته خالی است، لیست را خالی برمی‌گردانیم
        if (inputString.isEmpty()) {
            return storyArrayList
        }

        val elements = inputString.split(", ")

        for (element in elements) {
            val parts = element.split("~")
            if (parts.size == 30) {
                val date = parts[0].trim()
                val time = parts[1].trim()
                val reached = parts[2].trim()
                val engaged = parts[3].trim()
                val profileActivity = parts[4].trim()
                val reachedFollowers = parts[5].trim()
                val reachedNonFollowers = parts[6].trim()
                val impressions = parts[7].trim()
                val engagedFollowers = parts[8].trim()
                val engagedNonFollowers = parts[9].trim()
                val storyInteraction = parts[10].trim()
                val likes = parts[11].trim()
                val replies = parts[12].trim()
                val shares = parts[13].trim()
                val navigation = parts[14].trim()
                val forwards = parts[15].trim()
                val excited = parts[16].trim()
                val nextStory = parts[17].trim()
                val back = parts[18].trim()
                val profileVisits = parts[19].trim()
                val follows = parts[20].trim()
                val hasTaps = parts[21].trim()
                val tapName = parts[22].trim()
                val tapNumber = parts[23].trim()
                val hasLabel = parts[24].trim()
                val path = parts[25].trim()
                val image = parts[26].trim()
                val timestamp = parts[27].trim()
                val id = parts[28].trim()
                val showDate = parts[29].trim()

                // اضافه کردن یک Story جدید به لیست
                storyArrayList.add(
                    StoriesResponse.Data(
                        2,
                        id,
                        3,
                        image,
                        "",
                        image,
                        "",
                        caption = "",
                        timestamp,
                        story =
                        StoriesResponse.Story(
                            id,
                            date,
                            time,
                            reached,
                            engaged,
                            profileActivity,
                            reachedFollowers,
                            reachedNonFollowers,
                            impressions,
                            engagedFollowers,
                            engagedNonFollowers,
                            storyInteraction,
                            likes,
                            replies,
                            shares,
                            navigation,
                            forwards,
                            excited,
                            nextStory,
                            back,
                            profileVisits,
                            follows,
                            hasTaps.toBoolean(),
                            tapName,
                            tapNumber,
                            hasLabel.toBoolean(),
                            path,
                            showDate.toBoolean()
                        )
                    )

                )
            } else {
                // اگر فرمت رشته نادرست است، می‌توانید بر اساس نیاز خود عمل کنید (مثلاً خطا را رد کردن)
                // یا از اطلاعات دیگری استفاده کنید
            }
        }
        return storyArrayList
    }
}
