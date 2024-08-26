package com.shariati.instagrameditable.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.adapters.ImagePagerAdapter
import com.shariati.instagrameditable.adapters.InsightStoryAdapter
import com.shariati.instagrameditable.databinding.FragmentInsightBinding
import com.shariati.instagrameditable.models.StoriesResponse
import com.shariati.instagrameditable.utils.DepthPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import ir.mahozad.android.PieChart
import kotlin.random.Random

@AndroidEntryPoint
class InsightFragment : Fragment(), InsightStoryAdapter.InsightEvents {
    private lateinit var binding: FragmentInsightBinding
    private lateinit var story: StoriesResponse.Data
    private var storyPosition: Int = 0
    private lateinit var sharedPref: SharedPreferences

    private lateinit var adapter: ImagePagerAdapter

    private lateinit var storyArrayList: ArrayList<StoriesResponse.Data>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInsightBinding.inflate(layoutInflater)
        val bundle = arguments
        if (bundle != null) {
            storyPosition = bundle.getInt("storyPosition")
            story = bundle.getParcelable<StoriesResponse.Data>("story")!!

        }

        binding.close.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_down)
                .replace(R.id.fragment_container, StoryFragment())
                .commit()
        }


        sharedPref = requireContext().getSharedPreferences("story", Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fill storyArrayList if there is the story in shared preferences
        if (sharedPref.contains("storyArrayList")) {
            val storyRes: ArrayList<StoriesResponse.Data> =
                stringToStoryArrayList(
                    sharedPref.getString("storyArrayList", null).toString()
                )
            val p = ArrayList(storyRes.distinctBy { it.id })
            storyArrayList = p
        }

        val ss = storyArrayList.distinctBy { it.id }

        val filterList = ArrayList<StoriesResponse.Data>()

        ss.forEachIndexed { position, item ->
            if (item.timestamp.substring(0, 8) == story.timestamp.substring(0, 8)) {
                filterList.add(item)
            }
        }

        var s = filterList.distinctBy { it.id }

        val sss = s.sortedBy { it.timestamp }.toMutableList()

        val bundle = arguments

        val pp = bundle?.getInt("storyPosition")!!

        initViewPager(ArrayList(sss), pp)

        //set default insight value
        binding.insightReached.text = story.story.reached
        binding.insightAccountReached.text =
            (((story.story.reached).toInt() * 0.07).toInt() + story.story.reached.toInt()).toString()
        binding.insightAccountEngaged.text = story.story.engaged
        binding.insightProfileActivity.text = story.story.profileVisits
        binding.reachReachNumber.text = story.story.reached
        val randomRFN = Random.nextDouble(98.5,99.9)
        val randomRNFN = 100.0-randomRFN
        binding.reachFollowersNumber.text =String.format("%.1f", randomRFN)+"%"
        binding.reachNonFollowersNumber.text = String.format("%.1f", randomRNFN)+"%"
        val pieChart = binding.pieChartReached
        val rFollowersPercentage =
            (story.story.reachedFollowers.toFloat() / story.story.reached.toFloat())
        val rNOnFollowersPercentage =
            (story.story.reachedNonFollowers.toFloat() / story.story.reached.toFloat())

        pieChart.apply {
            slices = listOf(
                PieChart.Slice(randomRNFN.toFloat()/100, resources.getColor(R.color.blue_darker)),
                PieChart.Slice(randomRFN.toFloat()/100, resources.getColor(R.color.blue))
            )

            labelType = PieChart.LabelType.NONE
            //startAngle = 0
            isLegendsPercentageEnabled = true
            gradientType = PieChart.GradientType.SWEEP
            holeRatio = 0.8f
        }
        binding.reachImpresion.text = story.story.impressions
        binding.engagementEngagementNumber.text = story.story.engaged
        binding.engagedFollowersNumber.text = "100%"
        binding.engagedNonFollowersNumber.text = "0%"
        val pieChartEngaged = binding.pieChartEngaged
        val eFollowersPercentage =
            (story.story.engagedFollowers.toFloat() / story.story.engaged.toFloat())
        val eNOnFollowersPercentage =
            (story.story.engagedNonFollowers.toFloat() / story.story.engaged.toFloat())
        pieChartEngaged.apply {
            slices = listOf(
                PieChart.Slice(1f, resources.getColor(R.color.blue))
            )

            labelType = PieChart.LabelType.NONE
            //startAngle = 0
            isLegendsPercentageEnabled = true
            gradientType = PieChart.GradientType.SWEEP
            holeRatio = 0.8f
        }
        binding.storyInteraction.text = story.story.storyInteraction
        binding.storyLikes.text = story.story.likes
        binding.storyShares.text = story.story.shares
        binding.storyReplies.text = story.story.replies
        if (story.story.hasTaps) {
            binding.tapContainer.visibility = View.VISIBLE
            binding.nameTaps.text = story.story.tapName

            try {
                val tagNum = story.story.tapNumber.replace("\n" , ",")//23,25
                val listOfIntegers = tagNum.split(",").map { it.toInt() }
                val sumList = listOfIntegers.sum()

                binding.numberTaps.text = sumList.toString()
                binding.numberTapsChild.text = story.story.tapNumber
            }catch (e:Exception){
            }

        } else {
            binding.tapContainer.visibility = View.GONE
        }
        binding.navigation.text = story.story.navigation
        binding.navigationForwards.text = story.story.forwards
        binding.navigationNextStory.text = story.story.nextStory
        binding.navigationExite.text = story.story.excited
        binding.navigationBack.text = story.story.back
        binding.profileActivity.text = story.story.profileActivity
        //binding.profileActivityFollows.text = story.follows
        binding.profileActivityFollows.text = "0"
        binding.profileActivityVisit.text = story.story.profileActivity
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
                        thumbnail_url = image,
                        "",
                        image,
                        "",
                        "",
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


    fun initViewPager(list: ArrayList<StoriesResponse.Data>, position: Int) {
        list.add(list.size, StoriesResponse.Data(0,"0",0,"","IMAGE","https://biaupload.com/do.php?imgf=org-84bbf4fbc29e1.jpg","","it's video icon","",StoriesResponse.Story("0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0",false,"0","0",false,"0",false)))
        adapter = ImagePagerAdapter(list, requireContext(), binding.viewPager)

        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 4
        binding.viewPager.clipChildren = false
        binding.viewPager.clipToPadding = false
        binding.viewPager.setCurrentItem(position, false)
        //binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        binding.viewPager.setPageTransformer(CustomPageTransformer())
    }

    override fun setPostSize(story: ImageView, position: Int, storyReachedContainer: FrameLayout) {
    }
}

class CustomPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 0.8f + (1 - Math.abs(position)) * 0.2f

        // تغییر مقیاس
        page.scaleX = scaleFactor
        page.scaleY = scaleFactor

        // اطمینان از اینکه صفحه همیشه قابل مشاهده است
        page.visibility = if (position < -1 || position > 1) View.INVISIBLE else View.VISIBLE
    }
}


