package com.theprime.primegoplayer.utils

import android.app.Activity
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.theprime.primegoplayer.GoConstants
import com.theprime.primegoplayer.GoPreferences
import com.theprime.primegoplayer.R
import com.theprime.primegoplayer.extensions.enablePopupIcons
import com.theprime.primegoplayer.extensions.setTitle
import com.theprime.primegoplayer.extensions.setTitleColor
import com.theprime.primegoplayer.models.Music
import com.theprime.primegoplayer.player.MediaPlayerHolder
import com.theprime.primegoplayer.ui.MediaControlInterface
import com.theprime.primegoplayer.ui.UIControlInterface

object Popups {

    @JvmStatic
    fun showPopupForSongs(
        activity: Activity,
        itemView: View?,
        song: Music?,
        launchedBy: String
    ) {
        val mediaControlInterface = activity as MediaControlInterface
        itemView?.let { view ->

            PopupMenu(activity, view).apply {

                inflate(R.menu.popup_songs)

                menu.findItem(R.id.song_title).setTitle(activity.resources, song?.title)
                menu.enablePopupIcons(activity.resources)
                gravity = Gravity.END

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.favorites_add -> {
                            Lists.addToFavorites(
                                activity,
                                song,
                                canRemove = false,
                                0,
                                launchedBy
                            )
                            (activity as UIControlInterface).onFavoriteAddedOrRemoved()
                        }
                        else -> mediaControlInterface.onAddToQueue(song)
                    }
                    return@setOnMenuItemClickListener true
                }
                show()
            }
        }
    }

    @JvmStatic
    fun showPopupForPlaybackSpeed(activity: Activity, view: View) {

        PopupMenu(activity, view).apply {
            inflate(R.menu.popup_speed)
            gravity = Gravity.END

            if (GoPreferences.getPrefsInstance().playbackSpeedMode != GoConstants.PLAYBACK_SPEED_ONE_ONLY) {
                menu.findItem(getSelectedPlaybackItem(GoPreferences.getPrefsInstance().latestPlaybackSpeed)).setTitleColor(
                    Theming.resolveThemeColor(activity.resources)
                )
            }

            setOnMenuItemClickListener { menuItem ->
                val playbackSpeed = when (menuItem.itemId) {
                    R.id.speed_0 -> 0.25F
                    R.id.speed_1 -> 0.5F
                    R.id.speed_2 -> 0.75F
                    R.id.speed_3 -> 1.0F
                    R.id.speed_4 -> 1.25F
                    R.id.speed_5 -> 1.5F
                    R.id.speed_6 -> 1.75F
                    R.id.speed_7 -> 2.0F
                    R.id.speed_8 -> 2.5F
                    else -> 2.5F
                }
                if (GoPreferences.getPrefsInstance().playbackSpeedMode != GoConstants.PLAYBACK_SPEED_ONE_ONLY) {
                    menu.findItem(getSelectedPlaybackItem(playbackSpeed)).setTitleColor(
                        Theming.resolveThemeColor(activity.resources)
                    )
                }
                MediaPlayerHolder.getInstance().setPlaybackSpeed(playbackSpeed)
                return@setOnMenuItemClickListener true
            }
            show()
        }
    }

    private fun getSelectedPlaybackItem(playbackSpeed: Float) = when (playbackSpeed) {
        0.25F -> R.id.speed_0
        0.5F -> R.id.speed_1
        0.75F -> R.id.speed_2
        1.0F -> R.id.speed_3
        1.25F -> R.id.speed_4
        1.5F -> R.id.speed_5
        1.75F -> R.id.speed_6
        2.0F -> R.id.speed_7
        2.25F -> R.id.speed_8
        else -> R.id.speed_9
    }
}
