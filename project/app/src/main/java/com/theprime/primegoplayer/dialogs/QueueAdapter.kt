package com.theprime.primegoplayer.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theprime.primegoplayer.GoPreferences
import com.theprime.primegoplayer.R
import com.theprime.primegoplayer.databinding.QueueItemBinding
import com.theprime.primegoplayer.extensions.findIndex
import com.theprime.primegoplayer.extensions.startSongFromQueue
import com.theprime.primegoplayer.extensions.toName
import com.theprime.primegoplayer.models.Music
import com.theprime.primegoplayer.player.MediaPlayerHolder
import com.theprime.primegoplayer.utils.Theming


class QueueAdapter : RecyclerView.Adapter<QueueAdapter.QueueHolder>() {

    private val mediaPlayerHolder = MediaPlayerHolder.getInstance()
    var queueSongs = mediaPlayerHolder.queueSongs
    private var mSelectedSong = mediaPlayerHolder.currentSong

    var onQueueCleared: (() -> Unit)? = null

    fun swapSelectedSong(song: Music?) {
        notifyItemChanged(queueSongs.findIndex(mSelectedSong))
        mSelectedSong = song
        notifyItemChanged(queueSongs.findIndex(mSelectedSong))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueHolder {
        val binding = QueueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QueueHolder(binding)
    }

    override fun getItemCount() = queueSongs.size

    override fun onBindViewHolder(holder: QueueHolder, position: Int) {
        holder.bindItems(queueSongs[holder.absoluteAdapterPosition])
    }

    inner class QueueHolder(private val binding: QueueItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindItems(song: Music) {

            with(binding) {

                val context = root.context
                val displayedTitle = song.toName()

                title.text = displayedTitle
                duration.text = Dialogs.computeDurationText(context, song)
                subtitle.text =
                    context.getString(R.string.artist_and_album, song.artist, song.album)

                title.setTextColor(if (isCurrentIndex(absoluteAdapterPosition)){
                    Theming.resolveThemeColor(context.resources)
                } else {
                    Theming.resolveColorAttr(context, android.R.attr.textColorPrimary)
                })

                root.setOnClickListener {
                    with(mediaPlayerHolder) {
                        if (isQueue == null) isQueue = currentSong?.copy(startFrom = playerPosition)
                        startSongFromQueue(song)
                    }
                }
            }
        }
    }

    private fun isCurrentIndex(adapterPosition: Int): Boolean {
        val isQueue = mediaPlayerHolder.isQueue != null && mediaPlayerHolder.isQueueStarted
        return isQueue && queueSongs.findIndex(mSelectedSong) == adapterPosition
    }

    fun performQueueSongDeletion(context: Context, adapterPosition: Int): Boolean {
        val song = queueSongs[adapterPosition]
        val prefs = GoPreferences.getPrefsInstance()
        if (prefs.isAskForRemoval) {
            notifyItemChanged(adapterPosition)
            return if (song != mSelectedSong || mediaPlayerHolder.isQueue == null) {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.queue)
                    .setMessage(context.getString(
                        R.string.queue_song_remove,
                        song.title
                    ))
                    .setPositiveButton(R.string.yes) { _, _ ->
                        with(mediaPlayerHolder) {
                            // remove and update adapter
                            queueSongs.remove(song)
                            notifyItemRemoved(adapterPosition)

                            // dismiss sheet if empty
                            if (queueSongs.isEmpty()) {
                                isQueue = null
                                mediaPlayerInterface.onQueueStartedOrEnded(started = false)
                                onQueueCleared?.invoke()
                            }

                            // update queue songs
                            prefs.queue = queueSongs
                        }
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
                true
            } else {
                false
            }
        } else {
            return if (song != mSelectedSong || mediaPlayerHolder.isQueue == null) {
                queueSongs.remove(song)
                notifyItemRemoved(adapterPosition)
                true
            } else {
                false
            }
        }
    }
}
