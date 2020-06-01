package com.madlula.fastestresponse.ui

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.madlula.fastestresponse.R
import com.madlula.fastestresponse.databinding.FragmentGameBinding
import com.madlula.fastestresponse.viewModel.BaseViewModel
import com.madlula.fastestresponse.viewModel.Event


/**
 * Game fragment where tilt is detected.
 */
class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: BaseViewModel
    var numberOfRounds = 10
    var TAG = "Game fragment"

    lateinit var mOrientationEventListener: OrientationEventListener
    private var mDisplay: Display? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(requireActivity()).get(BaseViewModel::class.java)
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), getString(R.string.txt_game_begun), Toast.LENGTH_LONG).show()
        viewModel.getArrowDirection().observe(requireActivity(), Event.Observer {
            showArrow(it)
        })
        viewModel.getIntervalTime().observe(requireActivity(), Event.Observer {
            interval(it)
        })
        viewModel.generateInterval()
        viewModel.score.observe(requireActivity(), Observer {
            binding.txtScore.text = "" + it
        })

    }

    override fun onResume() {
        super.onResume()
        mOrientationEventListener = object : OrientationEventListener(requireContext(),
                SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                viewModel.detectedTilt(orientation)
                // Toast.makeText(requireContext(), "tilted : " + orientation, Toast.LENGTH_LONG).show()

            }
        }

        if (mOrientationEventListener.canDetectOrientation() === true) {
            Log.v("DEBUG_TAG", "Can detect orientation")
            mOrientationEventListener.enable()
        } else {
            Log.v("DEBUG_TAG", "Cannot detect orientation")
            mOrientationEventListener.disable()
        }
    }

    override fun onPause() {
        super.onPause()
        mOrientationEventListener.disable()
    }

    fun hideAllArrows() {
        viewModel.arrowShown = false
        binding.arrowForward.visibility = View.GONE
        binding.arrowRight.visibility = View.GONE
        binding.arrowBack.visibility = View.GONE
        binding.arrowLeft.visibility = View.GONE

    }

    fun showArrow(direction: Int) {
        var arrow: ImageView
        var colorList = (resources.getStringArray(R.array.color_list))
        when (direction) {
            1 -> arrow = binding.arrowForward
            2 -> arrow = binding.arrowRight
            3 -> arrow = binding.arrowBack
            4 -> arrow = binding.arrowLeft
            else -> arrow = binding.arrowForward
        }
        arrow.visibility = View.VISIBLE
        arrow.setColorFilter(requireActivity().getResources().getColor(viewModel.getColor()))
        ///arrow.setColorFilter(resources.getIdentifier(colorList[viewModel.choosenColor.value!!].toString(),"colors",requireContext().packageName))
        viewModel.arrowShown = true
        viewModel.waitingToShowArrow = false
        numberOfRounds--
        if (numberOfRounds != 0) {
            Handler().postDelayed({
                viewModel.generateInterval()
                //  Toast.makeText(requireContext(), "Round number : " + numberOfRounds, Toast.LENGTH_LONG).show()
            }, 3000)//only show arrow for 2 seconds
        } else {
            //game finished
            Handler().postDelayed({
                hideAllArrows()
                Toast.makeText(requireContext(), getString(R.string.txt_game_finished), Toast.LENGTH_LONG).show()
            }, 3000)

        }

    }

    fun interval(delayTime: Long) {
        hideAllArrows()
        // Toast.makeText(requireContext(),getString(R.string.txt_game_finished),Toast.LENGTH_LONG).show()
        viewModel.waitingToShowArrow = true
        viewModel.isTilted = false
        Handler().postDelayed({
            viewModel.generateDirection()
        }, delayTime)

    }


}
