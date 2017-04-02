package com.lineageinc.br0zip

import android.animation.Animator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class WinZipAdapter(context: Context, files: ArrayList<WinZipFile>) :
        ArrayAdapter<WinZipFile>(context, 0, files) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val itemView: View
        val file: WinZipFile = getItem(position)

        if (view == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        } else {
            itemView = view
        }

        val title = itemView.findViewById(R.id.title) as TextView
        title.text = file.title

        val icon = itemView.findViewById(R.id.icon) as ImageView
        if (file.isDirectory) {
            icon.setImageResource(R.drawable.ic_folder)
        } else if (file.title.endsWith(".zip") || file.title.endsWith(".img")) {
            icon.setImageResource(R.drawable.ic_zip)
        } else {
            icon.setImageResource(R.drawable.ic_file)
        }

        val background = itemView.findViewById(R.id.clicked)

        itemView.setOnClickListener { view ->
            if (file.title.endsWith(".zip") || file.title.endsWith(".img")) {
                animateItemPress(view, title, icon, background)
            } else if (file.isDirectory) {
                (context as MainActivity).navigateTo(file.title)
            } else {
                (context as MainActivity).notOpenable()
            }
        }

        return itemView
    }


    fun animateItemPress(view: View, title: TextView, icon: View, background: View) {
        background.background = ColorDrawable(ContextCompat.getColor(context, R.color.zip))
        background.visibility = View.VISIBLE

        val anim: Animator = ViewAnimationUtils.createCircularReveal(view,
                (icon.x + (icon.width / 2)).toInt(),
                (icon.y + (icon.height / 2)).toInt(),
                0f, Math.hypot(view.width.toDouble(), view.height.toDouble()).toFloat())
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 500
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                title.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            override fun onAnimationEnd(animation: Animator) {
                background.animate().alpha(0f).setDuration(50)
                        .withEndAction {
                            background.visibility = View.GONE
                            background.alpha = 1f
                            title.setTextColor(ContextCompat.getColor(context, R.color.black))
                            (context as MainActivity).winZipperRomPower()
                        }
                        .start()
            }
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
        })
        anim.start()
    }
}
