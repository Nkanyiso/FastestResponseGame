@file:Suppress("DEPRECATION")

package com.madlula.fastestresponse.ui.game

import android.annotation.SuppressLint
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
import com.madlula.fastestresponse.utilities.Constants
import com.madlula.fastestresponse.utilities.Constants.EMPTY_STRING
import com.madlula.fastestresponse.utilities.Constants.SPACE
import com.madlula.fastestresponse.utilities.Event
import com.madlula.fastestresponse.utilities.Utilities


/**
 * Game fragment where tilt is detected.
 */
@Suppress("DEPRECATION")
class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var orientationEventListener: OrientationEventListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(requireActivity()).get(GameViewModel::class.java)
        binding = FragmentGameBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getChosenColor().observe(viewLifecycleOwner,Event.Observer{
            binding.arrow.setColorFilter(requireActivity().resources.getColor(Utilities.getColor(it)))
        })
        viewModel.getArrowDirection().observe(viewLifecycleOwner, Event.Observer {
            showArrow(it)
        })
        viewModel.getNextArrow().observe(viewLifecycleOwner, Event.Observer {
            hideAllArrows()
        })
        viewModel.isGameFinished().observe(viewLifecycleOwner, Event.Observer {
            if (it) {
                hideAllArrows()
                showFinalScore()
            }
        })
        viewModel.isGameStarted().observe(requireActivity(), Event.Observer {
            if (it) {
                gameStart()
            }
        })
        viewModel.getScore().observe(viewLifecycleOwner, Observer {
            binding.txtScore.text =  EMPTY_STRING + it
        })

        viewModel.init(GameFragmentArgs.fromBundle(requireArguments()).chosenColor)


    }

    override fun onResume() {
        super.onResume()
        startWatchingOrientation()
       // orientationEventListener.enable()
    }

    override fun onPause() {
        super.onPause()
        orientationEventListener.disable()
    }

    private fun hideAllArrows() {
        binding.arrow.visibility = View.GONE


    }

    private fun showArrow(direction: Float) {
        binding.arrow.rotation = Constants.ARROW_ANGLES[0].toFloat() // first reset to original position
        binding.arrow.rotation = direction
        binding.arrow.visibility = View.VISIBLE


    }

    private fun startWatchingOrientation() {
        orientationEventListener = object : OrientationEventListener(requireContext(),
                SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                viewModel.detectedTilt(orientation)

            }
        }

        if (orientationEventListener.canDetectOrientation()) {
            Log.v("DEBUG_TAG", "Can detect orientation")
            orientationEventListener.enable()
        } else {
            Log.v("DEBUG_TAG", "Cannot detect orientation")
            orientationEventListener.disable()
        }
    }

    private fun gameStart() {
        hideAllArrows()
        Toast.makeText(requireContext(), getString(R.string.txt_game_begun), Toast.LENGTH_LONG).show()
    }




    private fun showFinalScore() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_final_score_title ))
                .setMessage(getString(R.string.txt_final_score) + SPACE + viewModel.getScore().value?.toString())
                .setPositiveButton(getString(R.string.btn_play_again)) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.init(GameFragmentArgs.fromBundle(requireArguments()).chosenColor)
                }
        builder.show()

    }

}
