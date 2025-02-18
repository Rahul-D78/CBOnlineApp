package com.codingblocks.cbonlineapp.course.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.getSpannableSring
import com.codingblocks.cbonlineapp.util.extensions.getSpannableStringSecondBold
import com.codingblocks.cbonlineapp.util.extensions.greater
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.onlineapi.models.Course
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_course_card.view.*
import kotlinx.android.synthetic.main.item_course_card.view.courseCardInstructorsTv
import kotlinx.android.synthetic.main.item_course_card.view.courseCardTitleTv
import kotlinx.android.synthetic.main.item_course_card.view.courseLogo
import kotlinx.android.synthetic.main.item_course_card.view.ratingTv
import kotlinx.android.synthetic.main.item_track_course.view.projectTitle
import kotlinx.android.synthetic.main.item_track_course.view.projectsChips
import kotlinx.android.synthetic.main.item_track_course.view.ratingBar
import kotlinx.android.synthetic.main.item_track_course.view.tagTitle
import kotlinx.android.synthetic.main.item_track_course.view.tagsChips
import org.jetbrains.anko.share

class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemClickListener: ItemClickListener? = null

    fun bind(item: Course, type: String) = with(itemView) {
        courseLogo.loadImage(item.logo)
        ViewCompat.setTransitionName(courseLogo, item.title)
        val ratingText = getSpannableSring("${item.rating}/5.0", ", ${item.reviewCount} ratings")
        ratingTv.text = ratingText
        courseCardTitleTv.text = item.title
        setOnClickListener {
            itemClickListener?.onClick(
                item.id, item.logo, courseLogo
            )
        }
        if (type == "TRACKS") {
            item.projects?.take(5)?.forEach {
                projectTitle.isVisible = true
                val chip = LayoutInflater.from(context).inflate(R.layout.single_chip_layout, tagsChips, false) as Chip
                chip.text = it.title
                projectsChips.addView(chip)
            }
            if (!item.instructors.isNullOrEmpty()) courseCardInstructorsTv.text = getSpannableStringSecondBold("Instructor: ", item.instructors?.first()?.name
                ?: "")
            if (item.instructors!!.size > 1) {
                courseCardInstructorsTv.append(" and ${item.instructors?.size!! - 1} more")
            }
            ratingBar.rating = item.rating
            item.tags?.take(5)?.forEach {
                tagTitle.isVisible = true
                val chip = Chip(context)
                chip.text = it.name
                val font = Typeface.createFromAsset(context.assets, "fonts/gilroy_medium.ttf")
                chip.typeface = font
                tagsChips.addView(chip)
            }
        } else {
            chip.text = when (item.difficulty) {
                "0" -> "Beginner"
                "1" -> "Advanced"
                "2" -> "Expert"
                else -> "Beginner"
            }

            if (type != "LIST") {
                courseCover.loadImage(item.coverImage ?: "")
            }
            if (type != "POPULAR") {
                if (!item.instructors.isNullOrEmpty()) {
                    courseCardInstructorsTv.text = getSpannableStringSecondBold("", item.instructors?.first()?.name
                        ?: "")
                    if (item.instructors!!.size > 1) {
                        courseCardInstructorsTv.append(" and ${item.instructors?.size!! - 1} more")
                    }
                    if (type != "LIST") {
                        course_card_share.setOnClickListener {
                            context.share("Check out the course *${item.title}* by Coding Blocks!\n\n" +
                                item.subtitle + "\n" +
                                "https://online.codingblocks.com/courses/${item.slug}/")
                        }
                        item.instructors?.first()?.photo?.let { courseCardInstructorImg1.loadImage(it) }
                        if (item.instructors!!.size > 1) {
                            courseCardInstructorImg2.isVisible = true
                            courseCardInstructorsTv.append("and ${item.instructors?.size!! - 1} more")
                            item.instructors!![1].photo?.let { courseCardInstructorImg2.loadImage(it) }
                        } else {
                            courseCardInstructorImg2.isVisible = false
                        }
                    }
                }
                var list = item.runs?.filter { run ->
                    !run.unlisted && run.enrollmentEnd.greater() && !run.enrollmentStart.greater()
                }?.sortedWith(compareBy { run -> run.price })
                if (list.isNullOrEmpty()) {
                    list =
                        item.runs?.sortedWith(compareBy { run -> run.price })
                }
                val price = list?.first()?.price
                courseCardPriceTv.text = if (price == "0") "FREE" else "₹ $price"
                courseCardMrpTv.text = "₹ " + list?.first()?.mrp
                courseCardMrpTv.paintFlags = courseCardPriceTv.paintFlags or
                    Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                if (type != "LIST") {

                    course_card_share.setOnClickListener {
                        context.share("https://online.codingblocks.com/app/courses/" + item.slug.toString())
                    }
                } else {
                }
            }
        }
    }
}
