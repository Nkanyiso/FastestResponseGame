package com.madlula.fastestresponse.ui

import android.content.DialogInterface
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.madlula.fastestresponse.R
import com.madlula.fastestresponse.utilities.Utilities
import com.madlula.fastestresponse.databinding.FragmentGameBinding
import com.madlula.fastestresponse.viewModel.GameViewModel
import com.madlula.fastestresponse.utilities.Event


/**
 * Game fragment where tilt is detected.
 */
class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    var TAG = "Game fragment"

    lateinit var mOrientationEventListener: OrientationEventListener
    private var mDisplay: Display? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(requireActivity()).get(GameViewModel::class.java)
        viewModel.init()
        binding = FragmentGameBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val args = GameFragmentArgs.fromBundle(it)
            viewModel.chosenColor.value = args.chosenColor
        }
        viewModel.getArrowDirection().observe(requireActivity(), Event.Observer {
            showArrow(it)
        })
        viewModel.getNextArrow().observe(requireActivity(), Event.Observer {
            hideAllArrows()
        })
        viewModel.isGameFinished().observe(requireActivity(), Event.Observer {
            gameFinished()
        })
        viewModel.isgameStarted().observe(requireActivity(), Event.Observer {
            gameStart()
        })
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
        when (direction) {
            1 -> arrow = binding.arrowForward
            2 -> arrow = binding.arrowRight
            3 -> arrow = binding.arrowBack
            4 -> arrow = binding.arrowLeft
            else -> arrow = binding.arrowForward
        }
        arrow.visibility = View.VISIBLE
        arrow.setColorFilter(requireActivity().getResources().getColor(Utilities.getColor(viewModel.chosenColor.value)))
        viewModel.arrowShown = true
        viewModel.waitingToShowArrow = false

    }

    fun gameStart() {
        hideAllArrows()
        Toast.makeText(requireContext(), getString(R.string.txt_game_begun), Toast.LENGTH_LONG).show()
    }

    fun gameFinished() {
        hideAllArrows()
        showFinalScore()
    }



    fun showFinalScore() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_final_score_title))
                .setMessage(getString(R.string.txt_final_score) + viewModel.score.value.toString())
                .setPositiveButton(getString(R.string.btn_play_again), DialogInterface.OnClickListener({ dialog, color ->
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_gameFragment_to_homeFragment)
                }))
        builder.show()
        builder.setCancelable(true)
    }

}
