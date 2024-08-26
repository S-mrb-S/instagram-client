package com.shariati.instagrameditable.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.adapters.StoryAdapter
import com.shariati.instagrameditable.data.repository.ApiRepository
import com.shariati.instagrameditable.databinding.BottomSheetStoryMenuBinding
import com.shariati.instagrameditable.databinding.DialogAddStoryBinding
import com.shariati.instagrameditable.databinding.FragmentArchiveBinding
import com.shariati.instagrameditable.models.StoriesResponse
import com.shariati.instagrameditable.models.Story
import com.shariati.instagrameditable.utils.getEndReach
import com.shariati.instagrameditable.utils.getGetData
import com.shariati.instagrameditable.utils.getStartReach
import com.shariati.instagrameditable.utils.isGetData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveFragment : Fragment(), StoryAdapter.StoryEvents {
    private lateinit var binding: FragmentArchiveBinding
    private lateinit var sharedPref: SharedPreferences

    //story variable
    private var storyInputStream: InputStream? = null
    private lateinit var storyAdapter: StoryAdapter
    private var storyArrayList: ArrayList<StoriesResponse.Data> = arrayListOf()
    private lateinit var storyBinding: DialogAddStoryBinding
    private var start :Int = 0
    private var end :Int = 0

    private var isRun = false
    private var previousReach = 0

    @Inject
    lateinit var api: ApiRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(layoutInflater)
        sharedPref = requireContext().getSharedPreferences("story", Context.MODE_PRIVATE)

        getStoryFromLocal()

        getStories()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         start = getStartReach(requireContext())
        end = getEndReach(requireContext())
        //set back icon
        binding.backToProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_down)
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        //add new story
        binding.addStoryArchive.setOnLongClickListener {

            storyBinding = DialogAddStoryBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(storyBinding.root)
                .setCancelable(true)
                .create()
            dialog.show()

            storyBinding.dialogAddStoryCancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            //set checked automatic insight change
            storyBinding.storyAutomaticInsight.setOnCheckedChangeListener { compoundButton, b ->
                if (b && storyBinding.storyReached.text!!.isNotEmpty()) {
                    //set random parameters
                    try {
                        val re = storyBinding.storyReached.text.toString().toInt()

                        val reached = re
                        //reach = 100
                        val randomEngaged = ((reached * 0.005).toInt() + (1 until 8).random())//1
                        val randomPActivity = (2 until 5).random()
                        val randomReachedFollowers =
                            ((reached * 0.95).toInt() until (reached * 0.98).toInt()).random()
                        val randomReachedNonFollowers = reached - randomReachedFollowers
                        val randomImpressions = ((reached * 0.10).toInt()) + reached
                        val randomEngagedFollowers = randomEngaged
                        val randomEngagedNonFollowers = 0
                        val randomStoryInteraction = randomEngaged
                        val randomLikes = randomEngaged
                        val randomReplies = 0
                        val randomShares = 0
                        val randomNavigation = ((reached * 0.20).toInt()) + reached//120
                        val randomForwards = ((randomNavigation * 0.83).toInt())//

                        val randomBack = (randomNavigation * 0.10).toInt()
                        val randomExited = ((randomNavigation) * 0.05).toInt()
                        val randomNextStory =
                            (((randomNavigation - randomForwards - randomExited - randomBack)))

                        val randomProfileVisits = randomPActivity
                        val randomFollows = 0

                        storyBinding.storyEngaged.setText(randomEngaged.toString())
                        storyBinding.storyProfileActivity.setText(randomPActivity.toString())
                        storyBinding.storyReachedFollowers.setText(randomReachedFollowers.toString())
                        storyBinding.storyReachedNonFollowers.setText(randomReachedNonFollowers.toString())
                        storyBinding.storyImpressions.setText(randomImpressions.toString())
                        storyBinding.storyEngagedFollowers.setText(randomEngagedFollowers.toString())
                        storyBinding.storyEngagedNonFollowers.setText(randomEngagedNonFollowers.toString())
                        storyBinding.storyStoryInteraction.setText(randomStoryInteraction.toString())
                        storyBinding.storyLikes.setText(randomLikes.toString())
                        storyBinding.storyReplies.setText(randomReplies.toString())
                        storyBinding.storyShares.setText(randomShares.toString())
                        storyBinding.storyNavigation.setText(randomNavigation.toString())
                        storyBinding.storyForwards.setText(randomForwards.toString())
                        storyBinding.storyExcited.setText(randomExited.toString())
                        storyBinding.storyNextStory.setText(randomNextStory.toString())
                        storyBinding.storyBack.setText(randomBack.toString())
                        storyBinding.storyProfileVisits.setText(randomProfileVisits.toString())
                        storyBinding.storyFollows.setText(randomFollows.toString())
                    } catch (ex: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "لطفا مقادیر را به درستی وارد فرمایید ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else if (storyBinding.storyReached.text!!.isNotEmpty()) {
                    storyBinding.storyEngaged.setText("")
                    storyBinding.storyProfileActivity.setText("")
                    storyBinding.storyReachedFollowers.setText("")
                    storyBinding.storyReachedNonFollowers.setText("")
                    storyBinding.storyImpressions.setText("")
                    storyBinding.storyEngagedFollowers.setText("")
                    storyBinding.storyEngagedNonFollowers.setText("")
                    storyBinding.storyStoryInteraction.setText("")
                    storyBinding.storyLikes.setText("")
                    storyBinding.storyReplies.setText("")
                    storyBinding.storyShares.setText("")
                    storyBinding.storyNavigation.setText("")
                    storyBinding.storyForwards.setText("")
                    storyBinding.storyExcited.setText("")
                    storyBinding.storyNextStory.setText("")
                    storyBinding.storyBack.setText("")
                    storyBinding.storyProfileVisits.setText("")
                    storyBinding.storyFollows.setText("")


                } else {
                    Toast.makeText(
                        requireContext(),
                        "لطفا مقدار Reached را وارد فرمایید",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            //set checked has tap change

            storyBinding.storyHasTaps.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    storyBinding.insightHasTapContainer.visibility = View.VISIBLE
                } else {
                    storyBinding.insightHasTapContainer.visibility = View.GONE

                }
            }
            //choose date and time
            storyBinding.chooseDate.setOnClickListener {
                val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select story date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setPositiveButtonText("Submit")
                    .build()
                materialDatePicker.addOnPositiveButtonClickListener { timeInMillis ->
                    Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show()
                    val date = Date(timeInMillis)
                    val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = simpleDateFormat.format(date)
                    storyBinding.chooseDate.text = formattedDate
                }

                materialDatePicker.addOnNegativeButtonClickListener {
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                }
                materialDatePicker.show(parentFragmentManager, "Material 2 Date Picker")
            }

            storyBinding.chooseTime.setOnClickListener {
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select story time")
                        .build()
                MaterialTimePicker.Builder().setInputMode(INPUT_MODE_CLOCK)
                picker.show(parentFragmentManager, "tag")
                picker.addOnPositiveButtonClickListener {
                    var hour: String = ""
                    var minute: String = ""
                    hour = if (picker.hour < 10) {
                        "0${picker.hour}"

                    } else {
                        picker.hour.toString()
                    }
                    minute = if (picker.minute < 10) {
                        "0${picker.minute}"
                    } else {
                        picker.minute.toString()

                    }

                    storyBinding.chooseTime.text = "$hour $minute"
                }

            }

            true
        }

        //Set the day on the calendar icon
        //@RequiresApi(Build.VERSION_CODES.O)
        binding.archiveDayTextView.text = LocalDate.now().dayOfMonth.toString()

    }


    private fun storyArrayListToString(storyArrayList: ArrayList<StoriesResponse.Data>): String {
        return storyArrayList.joinToString(", ") { "${it.story.date}~ ${it.story.time}~ ${it.story.reached}~ ${it.story.engaged}~ ${it.story.profileActivity}~ ${it.story.reachedFollowers}~ ${it.story.reachedNonFollowers}~ ${it.story.impressions}~ ${it.story.engagedFollowers}~ ${it.story.engagedNonFollowers}~ ${it.story.storyInteraction}~ ${it.story.likes}~ ${it.story.replies}~ ${it.story.shares}~ ${it.story.navigation}~ ${it.story.forwards}~ ${it.story.excited}~ ${it.story.nextStory}~ ${it.story.back}~ ${it.story.profileVisits}~ ${it.story.follows}~ ${it.story.hasTaps}~ ${it.story.tapName}~ ${it.story.tapNumber}~ ${it.story.hasLabel}~ ${it.story.path}~ ${it.thumbnail_url}~ ${it.timestamp}~ ${it.id}~ ${it.story.showDate}" }
    }

    private fun stringToStoryArrayList(inputString: String): ArrayList<Story> {
        val storyArrayList = ArrayList<Story>()

        // اگر رشته خالی است، لیست را خالی برمی‌گردانیم
        if (inputString.isEmpty()) {
            return storyArrayList
        }

        val elements = inputString.split(", ")

        for (element in elements) {
            val parts = element.split("~")
            if (parts.size == 26) {
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

                // اضافه کردن یک Story جدید به لیست
                storyArrayList.add(
                    Story(
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
                        path
                    )
                )
            } else {
                // اگر فرمت رشته نادرست است، می‌توانید بر اساس نیاز خود عمل کنید (مثلاً خطا را رد کردن)
                // یا از اطلاعات دیگری استفاده کنید
            }

        }

        return storyArrayList
    }

    override fun setStorySize(story: ImageView) {
        //set story image width and height accept ration : 9*16
        val storyWidth = (context?.resources?.displayMetrics?.widthPixels?.div(3)).toString()
        val storyHeight = (storyWidth.toInt() * 16 / 9).toString()

        if (storyWidth != null) {
            story.layoutParams.width = storyWidth.toInt()
            story.layoutParams.height = storyHeight.toInt()
        }
    }

    override fun storyEvent(position: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetStoryMenuBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.show()
        //set remove highlight event

        view.editPost.setOnClickListener {
            dialog.dismiss()
            storyBinding = DialogAddStoryBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(storyBinding.root)
                .setCancelable(true)
                .create()
            dialog.show()
            //set default story image from shared preferences

            val list = getArrayList()

            Glide.with(requireContext())
                .load(list[(list.size - 1) - position].thumbnail_url)
                .error(R.drawable.img_error)
                .into(storyBinding.storyImage)

            storyBinding.storyUrl.setText(list[(list.size - 1) - position].story.path)
            storyBinding.storyReached.setText(list[(list.size - 1) - position].story.reached)
            storyBinding.chooseDate.text = list[(list.size - 1) - position].story.date
            storyBinding.chooseTime.text = list[(list.size - 1) - position].story.time
            storyBinding.storyEngaged.setText(list[(list.size - 1) - position].story.engaged)
            storyBinding.storyProfileActivity.setText(list[(list.size - 1) - position].story.profileActivity)
            storyBinding.storyReachedFollowers.setText(list[(list.size - 1) - position].story.reachedFollowers)
            storyBinding.storyReachedNonFollowers.setText(list[(list.size - 1) - position].story.reachedNonFollowers)
            storyBinding.storyImpressions.setText(list[(list.size - 1) - position].story.impressions)
            storyBinding.storyEngagedFollowers.setText(list[(list.size - 1) - position].story.engagedFollowers)
            storyBinding.storyEngagedNonFollowers.setText(list[(list.size - 1) - position].story.engagedNonFollowers)
            storyBinding.storyStoryInteraction.setText(list[(list.size - 1) - position].story.storyInteraction)
            storyBinding.storyLikes.setText(list[(list.size - 1) - position].story.likes)
            storyBinding.storyReplies.setText(list[(list.size - 1) - position].story.replies)
            storyBinding.storyShares.setText(list[(list.size - 1) - position].story.shares)
            storyBinding.storyNavigation.setText(list[(list.size - 1) - position].story.navigation)
            storyBinding.storyForwards.setText(list[(list.size - 1) - position].story.forwards)
            storyBinding.storyExcited.setText(list[(list.size - 1) - position].story.excited)
            storyBinding.storyNextStory.setText(list[(list.size - 1) - position].story.nextStory)
            storyBinding.storyBack.setText(list[(list.size - 1) - position].story.back)
            storyBinding.storyProfileVisits.setText(list[(list.size - 1) - position].story.profileVisits)
            storyBinding.storyFollows.setText(list[(list.size - 1) - position].story.follows)
            storyBinding.storyHasTaps.isChecked = list[(list.size - 1) - position].story.hasTaps
            storyBinding.storyTapName.setText(list[(list.size - 1) - position].story.tapName)
            storyBinding.storyTapNumber.setText(list[(list.size - 1) - position].story.tapNumber)
            storyBinding.storyHasLabel.isChecked = list[(list.size - 1) - position].story.hasLabel

            storyBinding.dialogAddStoryCancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            storyBinding.storyAutomaticInsight.setOnCheckedChangeListener { compoundButton, b ->
                if (b && storyBinding.storyReached.text!!.isNotEmpty()) {
                    //set random parameters
                    try {
                        val reached = storyBinding.storyReached.text.toString().toInt()
                        //reach = 100
                        val randomEngaged = ((reached * 0.005).toInt())//1
                        val randomPActivity = (2 until 5).random()
                        val randomReachedFollowers =
                            ((reached * 0.95).toInt() until (reached * 0.98).toInt()).random()
                        val randomReachedNonFollowers = reached - randomReachedFollowers
                        val randomImpressions = ((reached * 0.10).toInt()) + reached
                        val randomEngagedFollowers = randomEngaged
                        val randomEngagedNonFollowers = 0
                        val randomStoryInteraction = (2 until 5).random()
                        val randomLikes = randomStoryInteraction
                        val randomReplies = 0
                        val randomShares = 0
                        val randomNavigation = ((reached * 0.20).toInt()) + reached//120
                        val randomForwards = ((randomNavigation * 0.83).toInt())//

                        val randomBack = (randomNavigation * 0.10).toInt()
                        val randomExited = ((randomNavigation) * 0.05).toInt()
                        val randomNextStory =
                            (((randomNavigation - randomForwards - randomExited - randomBack)))

                        val randomProfileVisits = randomPActivity
                        val randomFollows = 0

                        storyBinding.storyEngaged.setText(randomEngaged.toString())
                        storyBinding.storyProfileActivity.setText(randomPActivity.toString())
                        storyBinding.storyReachedFollowers.setText(randomReachedFollowers.toString())
                        storyBinding.storyReachedNonFollowers.setText(randomReachedNonFollowers.toString())
                        storyBinding.storyImpressions.setText(randomImpressions.toString())
                        storyBinding.storyEngagedFollowers.setText(randomEngagedFollowers.toString())
                        storyBinding.storyEngagedNonFollowers.setText(randomEngagedNonFollowers.toString())
                        storyBinding.storyStoryInteraction.setText(randomStoryInteraction.toString())
                        storyBinding.storyLikes.setText(randomLikes.toString())
                        storyBinding.storyReplies.setText(randomReplies.toString())
                        storyBinding.storyShares.setText(randomShares.toString())
                        storyBinding.storyNavigation.setText(randomNavigation.toString())
                        storyBinding.storyForwards.setText(randomForwards.toString())
                        storyBinding.storyExcited.setText(randomExited.toString())
                        storyBinding.storyNextStory.setText(randomNextStory.toString())
                        storyBinding.storyBack.setText(randomBack.toString())
                        storyBinding.storyProfileVisits.setText(randomProfileVisits.toString())
                        storyBinding.storyFollows.setText(randomFollows.toString())

                    } catch (ex: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "لطفا مقادیر را به درستی وارد فرمایید ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (storyBinding.storyReached.text!!.isNotEmpty()) {
                    storyBinding.storyEngaged.setText("")
                    storyBinding.storyProfileActivity.setText("")
                    storyBinding.storyReachedFollowers.setText("")
                    storyBinding.storyReachedNonFollowers.setText("")
                    storyBinding.storyImpressions.setText("")
                    storyBinding.storyEngagedFollowers.setText("")
                    storyBinding.storyEngagedNonFollowers.setText("")
                    storyBinding.storyStoryInteraction.setText("")
                    storyBinding.storyLikes.setText("")
                    storyBinding.storyReplies.setText("")
                    storyBinding.storyShares.setText("")
                    storyBinding.storyNavigation.setText("")
                    storyBinding.storyForwards.setText("")
                    storyBinding.storyExcited.setText("")
                    storyBinding.storyNextStory.setText("")
                    storyBinding.storyBack.setText("")
                    storyBinding.storyProfileVisits.setText("")
                    storyBinding.storyFollows.setText("")
                } else {
                    Toast.makeText(
                        requireContext(),
                        "لطفا مقدار Reached را وارد فرمایید",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            //set checked has tap change

            storyBinding.storyHasTaps.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    storyBinding.insightHasTapContainer.visibility = View.VISIBLE
                } else {
                    storyBinding.insightHasTapContainer.visibility = View.GONE

                }
            }
            //choose date and time
            storyBinding.chooseDate.setOnClickListener {
                val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select story date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setPositiveButtonText("Submit")
                    .build()
                materialDatePicker.addOnPositiveButtonClickListener { timeInMillis ->
                    Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show()
                    val date = Date(timeInMillis)
                    val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = simpleDateFormat.format(date)
                    storyBinding.chooseDate.text = formattedDate
                }

                materialDatePicker.addOnNegativeButtonClickListener {
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                }
                materialDatePicker.show(parentFragmentManager, "Material 2 Date Picker")
            }


            storyBinding.chooseTime.setOnClickListener {
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select story time")
                        .build()
                MaterialTimePicker.Builder().setInputMode(INPUT_MODE_CLOCK)
                picker.show(parentFragmentManager, "tag")
                picker.addOnPositiveButtonClickListener {
                    var hour: String = ""
                    var minute: String = ""
                    hour = if (picker.hour < 10) {
                        "0${picker.hour}"

                    } else {
                        picker.hour.toString()
                    }
                    minute = if (picker.minute < 10) {
                        "0${picker.minute}"
                    } else {
                        picker.minute.toString()

                    }
                    storyBinding.chooseTime.text = "$hour $minute"
                }

            }
            storyBinding.dialogAddStoryConfirmBtn.setOnClickListener {

                if (!storyBinding.storyReached.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyEngaged.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyProfileActivity.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyReachedFollowers.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyReachedNonFollowers.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyImpressions.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyEngagedFollowers.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyEngagedNonFollowers.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyStoryInteraction.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyLikes.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyReplies.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyShares.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyNavigation.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyForwards.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyExcited.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyNextStory.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyBack.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyProfileVisits.text.toString()
                        .isNullOrEmpty() && !storyBinding.storyFollows.text.toString()
                        .isNullOrEmpty()
                ) {
                    if (storyBinding.storyHasTaps.isChecked && !storyBinding.storyTapName.text.toString()
                            .isNullOrEmpty() && !storyBinding.storyTapNumber.text.toString()
                            .isNullOrEmpty()
                    ) {
                        if (storyBinding.storyHasLabel.isChecked) {

                            val ar = getArrayList()
                            ar[(storyArrayList.size - 1) - position] =
                                StoriesResponse.Data(
                                    comments_count = ar[(storyArrayList.size - 1) - position].comments_count,
                                    id = ar[(storyArrayList.size - 1) - position].id,
                                    like_count = ar[(storyArrayList.size - 1) - position].like_count,
                                    thumbnail_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    username = ar[(storyArrayList.size - 1) - position].username,
                                    timestamp = ar[(storyArrayList.size - 1) - position].timestamp,
                                    media_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    media_type = ar[(storyArrayList.size - 1) - position].media_type,
                                    caption = "",
                                    story = StoriesResponse.Story(
                                        ar[(storyArrayList.size - 1) - position].id,
                                        storyBinding.chooseDate.text.toString(),
                                        storyBinding.chooseTime.text.toString(),
                                        storyBinding.storyReached.text.toString(),
                                        storyBinding.storyEngaged.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyReachedFollowers.text.toString(),
                                        storyBinding.storyReachedNonFollowers.text.toString(),
                                        storyBinding.storyImpressions.text.toString(),
                                        storyBinding.storyEngagedFollowers.text.toString(),
                                        storyBinding.storyEngagedNonFollowers.text.toString(),
                                        storyBinding.storyStoryInteraction.text.toString(),
                                        storyBinding.storyLikes.text.toString(),
                                        storyBinding.storyReplies.text.toString(),
                                        storyBinding.storyShares.text.toString(),
                                        storyBinding.storyNavigation.text.toString(),
                                        storyBinding.storyForwards.text.toString(),
                                        storyBinding.storyExcited.text.toString(),
                                        storyBinding.storyNextStory.text.toString(),
                                        storyBinding.storyBack.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyFollows.text.toString(),
                                        true,
                                        storyBinding.storyTapName.text.toString(),
                                        storyBinding.storyTapNumber.text.toString(),
                                        true,
                                        storyBinding.storyUrl.text.toString(),
                                        ar[(storyArrayList.size - 1) - position].story.showDate
                                    )
                                )

                            storyAdapter.notifyItemChanged(position)
                            sharedPref.edit().putString(
                                "storyArrayList",
                                storyArrayListToString(ar)
                            ).apply()
                            dialog.dismiss()

                        } else {
                            val ar = getArrayList()
                            ar[(storyArrayList.size - 1) - position] =
                                StoriesResponse.Data(
                                    comments_count = ar[(storyArrayList.size - 1) - position].comments_count,
                                    id = ar[(storyArrayList.size - 1) - position].id,
                                    like_count = ar[(storyArrayList.size - 1) - position].like_count,
                                    thumbnail_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    username = ar[(storyArrayList.size - 1) - position].username,
                                    timestamp = ar[(storyArrayList.size - 1) - position].timestamp,
                                    media_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    media_type = ar[(storyArrayList.size - 1) - position].media_type,
                                    caption = "",
                                    story = StoriesResponse.Story(
                                        ar[(storyArrayList.size - 1) - position].id,
                                        storyBinding.chooseDate.text.toString(),
                                        storyBinding.chooseTime.text.toString(),
                                        storyBinding.storyReached.text.toString(),
                                        storyBinding.storyEngaged.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyReachedFollowers.text.toString(),
                                        storyBinding.storyReachedNonFollowers.text.toString(),
                                        storyBinding.storyImpressions.text.toString(),
                                        storyBinding.storyEngagedFollowers.text.toString(),
                                        storyBinding.storyEngagedNonFollowers.text.toString(),
                                        storyBinding.storyStoryInteraction.text.toString(),
                                        storyBinding.storyLikes.text.toString(),
                                        storyBinding.storyReplies.text.toString(),
                                        storyBinding.storyShares.text.toString(),
                                        storyBinding.storyNavigation.text.toString(),
                                        storyBinding.storyForwards.text.toString(),
                                        storyBinding.storyExcited.text.toString(),
                                        storyBinding.storyNextStory.text.toString(),
                                        storyBinding.storyBack.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyFollows.text.toString(),
                                        true,
                                        storyBinding.storyTapName.text.toString(),
                                        storyBinding.storyTapNumber.text.toString(),
                                        false,
                                        storyBinding.storyUrl.text.toString(),
                                        ar[(storyArrayList.size - 1) - position].story.showDate
                                    )
                                )

                            // storyAdapter.notifyItemChanged(position)
                            sharedPref.edit().putString(
                                "storyArrayList",
                                storyArrayListToString(ar)
                            ).apply()
                            dialog.dismiss()
                        }

                    } else if (!storyBinding.storyHasTaps.isChecked) {
                        if (storyBinding.storyHasLabel.isChecked) {

                            val ar = getArrayList()

                            ar[(storyArrayList.size - 1) - position] =
                                StoriesResponse.Data(
                                    comments_count = ar[(storyArrayList.size - 1) - position].comments_count,
                                    id = ar[(storyArrayList.size - 1) - position].id,
                                    like_count = ar[(storyArrayList.size - 1) - position].like_count,
                                    thumbnail_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    username = ar[(storyArrayList.size - 1) - position].username,
                                    timestamp = ar[(storyArrayList.size - 1) - position].timestamp,
                                    media_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    media_type = ar[(storyArrayList.size - 1) - position].media_type,
                                    caption = "",
                                    story = StoriesResponse.Story(
                                        ar[(storyArrayList.size - 1) - position].id,
                                        storyBinding.chooseDate.text.toString(),
                                        storyBinding.chooseTime.text.toString(),
                                        storyBinding.storyReached.text.toString(),
                                        storyBinding.storyEngaged.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyReachedFollowers.text.toString(),
                                        storyBinding.storyReachedNonFollowers.text.toString(),
                                        storyBinding.storyImpressions.text.toString(),
                                        storyBinding.storyEngagedFollowers.text.toString(),
                                        storyBinding.storyEngagedNonFollowers.text.toString(),
                                        storyBinding.storyStoryInteraction.text.toString(),
                                        storyBinding.storyLikes.text.toString(),
                                        storyBinding.storyReplies.text.toString(),
                                        storyBinding.storyShares.text.toString(),
                                        storyBinding.storyNavigation.text.toString(),
                                        storyBinding.storyForwards.text.toString(),
                                        storyBinding.storyExcited.text.toString(),
                                        storyBinding.storyNextStory.text.toString(),
                                        storyBinding.storyBack.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyFollows.text.toString(),
                                        false,
                                        "",
                                        "",
                                        true,
                                        storyBinding.storyUrl.text.toString(),
                                        ar[(storyArrayList.size - 1) - position].story.showDate
                                    )
                                )

                            // storyAdapter.notifyItemChanged(position)
                            sharedPref.edit().putString(
                                "storyArrayList",
                                storyArrayListToString(ar)
                            ).apply()

                            getStoryFromLocal()

                            dialog.dismiss()
                        } else {
                            val ar = getArrayList()

                            ar[(storyArrayList.size - 1) - position] =
                                StoriesResponse.Data(
                                    comments_count = ar[(storyArrayList.size - 1) - position].comments_count,
                                    id = ar[(storyArrayList.size - 1) - position].id,
                                    like_count = ar[(storyArrayList.size - 1) - position].like_count,
                                    thumbnail_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    username = ar[(storyArrayList.size - 1) - position].username,
                                    timestamp = ar[(storyArrayList.size - 1) - position].timestamp,
                                    media_url = ar[(storyArrayList.size - 1) - position].media_url,
                                    media_type = ar[(storyArrayList.size - 1) - position].media_type,
                                    caption = "",
                                    story = StoriesResponse.Story(
                                        ar[(storyArrayList.size - 1) - position].id,
                                        storyBinding.chooseDate.text.toString(),
                                        storyBinding.chooseTime.text.toString(),
                                        storyBinding.storyReached.text.toString(),
                                        storyBinding.storyEngaged.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyReachedFollowers.text.toString(),
                                        storyBinding.storyReachedNonFollowers.text.toString(),
                                        storyBinding.storyImpressions.text.toString(),
                                        storyBinding.storyEngagedFollowers.text.toString(),
                                        storyBinding.storyEngagedNonFollowers.text.toString(),
                                        storyBinding.storyStoryInteraction.text.toString(),
                                        storyBinding.storyLikes.text.toString(),
                                        storyBinding.storyReplies.text.toString(),
                                        storyBinding.storyShares.text.toString(),
                                        storyBinding.storyNavigation.text.toString(),
                                        storyBinding.storyForwards.text.toString(),
                                        storyBinding.storyExcited.text.toString(),
                                        storyBinding.storyNextStory.text.toString(),
                                        storyBinding.storyBack.text.toString(),
                                        storyBinding.storyProfileActivity.text.toString(),
                                        storyBinding.storyFollows.text.toString(),
                                        false,
                                        "",
                                        "",
                                        false,
                                        storyBinding.storyUrl.text.toString(),
                                        ar[(storyArrayList.size - 1) - position].story.showDate
                                    )
                                )

                            sharedPref.edit().putString(
                                "storyArrayList",
                                storyArrayListToString(ArrayList(ar))
                            ).apply()

                            getStoryFromLocal()

                            //storyAdapter.notifyItemChanged((storyArrayList.size -1) - position)

                            dialog.dismiss()
                        }
                    }

                } else {
                    Toast.makeText(
                        context,
                        "لطفا تصویر مورد نیاز را مجدد وارد فرمایید و تمام مقادیر را پر کنید",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }


    fun getPositionItem(
        filterList: ArrayList<StoriesResponse.Data>,
        existing: List<StoriesResponse.Data>,
        position: Int
    ): Int {

        val p = filterList.indexOf(
            StoriesResponse.Data(
                existing[position].comments_count,
                existing[position].id,
                existing[position].like_count,
                existing[position].thumbnail_url,
                existing[position].media_type,
                existing[position].media_url,
                existing[position].username,
                caption = "",
                existing[position].timestamp,
                story = StoriesResponse.Story(
                    existing[position].story.id,
                    existing[position].story.date,
                    existing[position].story.time,
                    existing[position].story.reached,
                    existing[position].story.engaged,
                    existing[position].story.profileActivity,
                    existing[position].story.reachedFollowers,
                    existing[position].story.reachedNonFollowers,
                    existing[position].story.impressions,
                    existing[position].story.engagedFollowers,
                    existing[position].story.engagedNonFollowers,
                    existing[position].story.storyInteraction,
                    existing[position].story.likes,
                    existing[position].story.replies,
                    existing[position].story.shares,
                    existing[position].story.navigation,
                    existing[position].story.forwards,
                    existing[position].story.excited,
                    existing[position].story.nextStory,
                    existing[position].story.back,
                    existing[position].story.profileVisits,
                    existing[position].story.follows,
                    existing[position].story.hasTaps,
                    existing[position].story.tapName,
                    existing[position].story.tapNumber,
                    existing[position].story.hasLabel,
                    existing[position].story.path,
                    existing[position].story.showDate,
                )
            )
        )
        return p
    }

    override fun showStory(view: ImageView, position: Int) {

        val fragment = StoryFragment()

        val existingArrayList = getArrayList().sortedByDescending { it.timestamp }

        val existing = existingArrayList.distinctBy { it.id }

        /*   sharedPref.edit()
               .putString("storyArrayList", storyArrayListToString(ArrayList(s)))
               .apply()*/

        val filterList = ArrayList<StoriesResponse.Data>()

        existing.forEachIndexed { p, item ->
            if (item.timestamp.substring(0, 8) == existing[position].timestamp.substring(0, 8)) {
                filterList.add(item)
            }
        }

        val itemFindPosition = getPositionItem(filterList, existing, position)


        val bundle = Bundle()
        bundle.putParcelable("story", filterList[itemFindPosition])
        bundle.putInt("storyPosition", itemFindPosition)
        bundle.putString("imageStory", filterList[itemFindPosition].thumbnail_url)
        bundle.putInt("storySize", filterList.size)

        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_up,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_down
            )
//           .addSharedElement(view,"transition$position")
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
private var isFirstOpening = true
    private fun getStories() {
        getStoryFromLocal()
        if(isFirstOpening){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressbarLoading.visibility = View.GONE
            binding.storyArchiveRecyclerContainer.visibility = View.VISIBLE
            isFirstOpening = false
        },1000)}
        else{
            binding.progressbarLoading.visibility = View.GONE
            binding.storyArchiveRecyclerContainer.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val stories = api.getStories()
            when (stories.code()) {
                200 -> {
                    if (getGetData(requireContext())) {
                        stories.body()?.let { it ->
                            val storyList = ArrayList<StoriesResponse.Data>()
                            for (story in it.data) {

                                if (story.caption?.isEmpty() == false) {
                                    val regex = Regex("@[a-zA-Z0-9._]+")
                                    val matches =
                                        regex.findAll(story.caption.toString().replace("  ", "\n"))
                                    val tags = matches.map { it.value }.toList()

                                    if (tags.size > 1) {
                                        val emptyListStory = setDataAuto(
                                            true,
                                            tags.joinToString().replace(", ", "\n"),
                                            tags.size
                                        )
                                        story.story = emptyListStory
                                    } else {
                                        val emptyListStory = setDataAuto(
                                            true,
                                            tags.joinToString().replace("\n", ""),
                                            tags.size
                                        )
                                        story.story = emptyListStory
                                    }

                                    /*    val pattern = Pattern.compile("@\\w+")
                                        val matcher = pattern.matcher(story.caption)
                                        if (matcher.find()) {
                                            val result = matcher.group()
                                            val emptyListStory = setDataAuto(true, result)
                                            story.story = emptyListStory
                                        }*/
                                } else {
                                    val emptyListStory = setDataAuto(false, "", 0)
                                    story.story = emptyListStory
                                }

                                isRun = true
                                story.timestamp =
                                    story.timestamp.substring(0, 16).replace("T", "")
                                        .replace("-", "")
                                        .replace(":", "")
                                story.story.id = story.id
                                if (story.media_type == "IMAGE")
                                    story.thumbnail_url = story.media_url
                                else story.thumbnail_url = story.thumbnail_url
                                storyList.add(story)
                                addNewItemIfNotExists(requireContext(), story)
                            }
                            isRun = false

                            isGetData(requireContext(), false)

                            val so = getArrayList().distinctBy { it.id }

                            storyArrayList = ArrayList(so)

                            var listWithDate = ArrayList<StoriesResponse.Data>()

                            val v = storyArrayList

                            listWithDate = ArrayList(v)

                            storyArrayList.forEachIndexed { position, item ->
                                if (position == 0) {
                                    listWithDate[position].story.showDate = true
                                } else {
                                    if (item.timestamp.substring(
                                            0,
                                            8
                                        ) == listWithDate[position - 1].timestamp.substring(0, 8)
                                    ) {
                                        listWithDate[position].story.showDate = false
                                    } else {
                                        listWithDate[position].story.showDate = true
                                    }
                                }
                            }

                            val l = listWithDate

                            val sortList = l.sortedByDescending { it.timestamp }

                            val lm: GridLayoutManager =
                                object : GridLayoutManager(requireContext(), 3) {
                                    override fun isLayoutRTL(): Boolean {
                                        return true
                                    }
                                }

                            lm.reverseLayout = true

                            binding.storyArchiveRecyclerContainer.layoutManager = lm

                            storyAdapter = StoryAdapter(
                                ArrayList(sortList),
                                this@ArchiveFragment,
                                requireContext()
                            )

                            binding.storyArchiveRecyclerContainer.adapter = null
                            binding.storyArchiveRecyclerContainer.adapter = storyAdapter

                        }
                    }
                }
            }
        }
    }

    fun addNewItemIfNotExists(context: Context, newItem: StoriesResponse.Data) {
        val existingArrayList = getData(
            sharedPref.getString("storyArrayList", null).toString()
        )

        // Check if the item already exists in the list
        if (!isItemInArrayList(existingArrayList, newItem)) {
            existingArrayList.add(newItem)
            saveArrayList(context, existingArrayList)
        } else {
            Log.i("", "")
        }
    }

    private fun isItemInArrayList(
        arrayList: ArrayList<StoriesResponse.Data>, newItem: StoriesResponse.Data
    ): Boolean {
        val array = arrayList.distinctBy { id }
        return array.contains(newItem)
    }

    fun saveArrayList(context: Context, arrayList: ArrayList<StoriesResponse.Data>) {

        sharedPref.edit()
            .putString("storyArrayList", storyArrayListToString(arrayList))
            .apply()
    }

    fun getArrayList(): ArrayList<StoriesResponse.Data> {
        val storyRes: ArrayList<StoriesResponse.Data> = getData(
            sharedPref.getString("storyArrayList", null).toString()
        )

        val so = storyRes.distinctBy { it.id }

        return ArrayList(so.sortedBy { it.timestamp })
    }

    fun getStoryFromLocal() {
        val story = getArrayList()

        var listWithDate = ArrayList<StoriesResponse.Data>()

        val v = story

        listWithDate = ArrayList(v)

        storyArrayList.forEachIndexed { position, item ->
            if (position == 0) {
                listWithDate[position].story.showDate = true
            } else {
                if (item.timestamp.substring(
                        0,
                        8
                    ) == listWithDate[position - 1].timestamp.substring(0, 8)
                ) {
                    listWithDate[position].story.showDate = false
                } else {
                    listWithDate[position].story.showDate = true
                }
            }
        }

        val l = listWithDate

        val sortList = l.sortedByDescending { it.timestamp }


        val lm: GridLayoutManager = object : GridLayoutManager(requireContext(), 3) {

            override fun isLayoutRTL(): Boolean {
                return true
            }
        }

        lm.reverseLayout = true

        binding.storyArchiveRecyclerContainer.layoutManager = lm
        storyAdapter = StoryAdapter(ArrayList(sortList), this@ArchiveFragment, requireContext())

        binding.storyArchiveRecyclerContainer.adapter = null
        binding.storyArchiveRecyclerContainer.adapter = storyAdapter
    }


    private fun setDataAuto(
        isHasTaps: Boolean,
        tapName: String,
        tagSizeList: Int
    ): StoriesResponse.Story {

        val view = (start..end).random()
       start = view
        val reached = view
        //reach = 100
        val randomEngaged = ((reached * 0.005).toInt() + (1 until 8).random())//1
        val randomPActivity = (2 until 5).random()
        val randomReachedFollowers =
            ((reached * 0.95).toInt() until (reached * 0.98).toInt()).random()
        val randomReachedNonFollowers = reached - randomReachedFollowers
        val randomImpressions = ((reached * 0.10).toInt()) + reached
        val randomEngagedFollowers = randomEngaged
        val randomEngagedNonFollowers = 0
        val randomStoryInteraction = (2 until 5).random()
        val randomLikes = randomStoryInteraction
        val randomReplies = 0
        val randomShares = 0
        val randomNavigation = ((reached * 0.20).toInt()) + reached//120
        val randomForwards = ((randomNavigation * 0.83).toInt())//

        val randomBack = (randomNavigation * 0.10).toInt()
        val randomExited = ((randomNavigation) * 0.05).toInt()
        val randomNextStory = (((randomNavigation - randomForwards - randomExited - randomBack)))

        val randomProfileVisits = randomPActivity
        val randomFollows = 0

        var tagNumber = "0"

        if (isHasTaps) {
            val tagNumList = mutableListOf<Int>()
            repeat(tagSizeList) {
                tagNumList.add(it + (9 until 17).random())
            }
            val l = tagNumList.sortedDescending()
            tagNumber = l.joinToString().replace(", ", "\n")
        }

        return StoriesResponse.Story(
            "",
            "",
            "",
            reached.toString(),
            randomEngaged.toString(),
            randomPActivity.toString(),
            randomReachedFollowers.toString(),
            randomReachedNonFollowers.toString(),
            randomImpressions.toString(),
            randomEngagedFollowers.toString(),
            randomEngagedNonFollowers.toString(),
            randomStoryInteraction.toString(),
            randomLikes.toString(),
            randomReplies.toString(),
            randomShares.toString(),
            randomNavigation.toString(),
            randomForwards.toString(),
            randomExited.toString(),
            randomNextStory.toString(),
            randomBack.toString(),
            randomProfileVisits.toString(),
            randomFollows.toString(),
            isHasTaps,
            if (isHasTaps) tapName else "",
            if (isHasTaps) tagNumber else "0",
            false,
            "",
            false
        )
    }

    private fun getData(inputString: String): ArrayList<StoriesResponse.Data> {
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
                        comments_count = 2,
                        id = id,
                        like_count = 3,
                        thumbnail_url = image,
                        media_type = "",
                        media_url = image,
                        timestamp = timestamp,
                        username = "",
                        caption = "",
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