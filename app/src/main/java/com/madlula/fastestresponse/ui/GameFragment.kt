package com.madlula.fastestresponse.ui

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.madlula.fastestresponse.databinding.FragmentGameBinding
import com.madlula.fastestresponse.viewModel.BaseViewModel
import com.madlula.fastestresponse.viewModel.Event

/**
 * Game fragment where tilt is detected.
 */
class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: BaseViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(requireActivity()).get(BaseViewModel::class.java)
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getArrowDirection().observe(requireActivity(), Event.Observer {
            showArrow(it)
        })
        viewModel.generateDirection()
    }

    fun hideAllArrows() {
        binding.arrowForward.visibility = View.GONE
        binding.arrowRight.visibility = View.GONE
        binding.arrowBack.visibility = View.GONE
        binding.arrowLeft.visibility = View.GONE

    }

    fun showArrow(direction: Int) {
        var arrow: ImageView
        when (direction) {
            1 -> arrow = binding.arrowForward
            2 -> arrow = binding.arrowRight
            3 -> arrow = binding.arrowBack
            4 -> arrow = binding.arrowLeft
            else -> arrow = binding.arrowForward
        }
        arrow.visibility = View.VISIBLE

    }

}
