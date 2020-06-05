package com.madlula.fastestresponse.ui.game

import android.content.DialogInterface
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.madlula.fastestresponse.R
import com.madlula.fastestresponse.databinding.FragmentGameBinding
import com.madlula.fastestresponse.utilities.Event
import com.madlula.fastestresponse.utilities.Utilities


/**
 * Game fragment where tilt is detected.
 */
class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    lateinit var mOrientationEventListener: OrientationEventListener

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
        binding.arrow.setColorFilter(requireActivity().getResources().getColor(Utilities.getColor(viewModel.chosenColor.value)))
        viewModel.getArrowDirection().observe(viewLifecycleOwner, Event.Observer {
            showArrow(it)
        })
        viewModel.getNextArrow().observe(viewLifecycleOwner, Event.Observer {
            hideAllArrows()
        })
        viewModel.isGameFinished().observe(viewLifecycleOwner, Event.Observer {
            if(it){
            gameFinished()
            }
        })
        viewModel.isgameStarted().observe(requireActivity(), Event.Observer {
            if(it){
            gameStart()
        }
        })
        viewModel.score.observe(viewLifecycleOwner, Observer {
            binding.txtScore.text = "" + it
        })
        startWatchingOrientation();



    }



    override fun onPause() {
        super.onPause()
        mOrientationEventListener.disable()
    }

    private fun hideAllArrows() {
        viewModel.arrowShown = false
        binding.arrow.visibility = View.GONE


    }

    private fun showArrow(direction: Float) {
        binding.arrow.rotation = 0f // first reset to original position
        binding.arrow.rotation = direction
        binding.arrow.visibility = View.VISIBLE
        viewModel.arrowShown = true
        viewModel.waitingToShowArrow = false

    }
    private fun startWatchingOrientation(){
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
    private fun gameStart() {

        hideAllArrows()
        Toast.makeText(requireContext(), getString(R.string.txt_game_begun), Toast.LENGTH_LONG).show()
    }

    private fun gameFinished() {
        hideAllArrows()
        showFinalScore()
    }


    private fun showFinalScore() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_final_score_title))
                .setMessage(getString(R.string.txt_final_score) + viewModel.score.value.toString())
                .setPositiveButton(getString(R.string.btn_play_again), DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    viewModel.init()
                })
        builder.show()
        builder.setCancelable(true)
    }

}
