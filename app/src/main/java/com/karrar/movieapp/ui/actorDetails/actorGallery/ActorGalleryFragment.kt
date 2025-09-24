package com.karrar.movieapp.ui.actorDetails.actorGallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentActorGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActorGalleryFragment : Fragment() {
    private lateinit var binding: FragmentActorGalleryBinding
    val args: ActorGalleryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentActorGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTitle()
        val actorImages = args.actorImages.toList()
        val actorGalleryImages =
            actorImages
                .chunked(3)
                .map { GalleryItem(it.toList()) }
        binding.recyclerViewActorImages.adapter = ActorImageAdapter(actorGalleryImages)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewActorImages.layoutManager = layoutManager
    }

    private fun setTitle() {
        (activity as AppCompatActivity).supportActionBar?.show()
        args.actorName.let {
            (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.actor_gallery, it)
        }
    }
}
