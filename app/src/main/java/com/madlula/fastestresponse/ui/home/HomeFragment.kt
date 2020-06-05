@file:Suppress("DEPRECATION")

package com.madlula.fastestresponse.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.madlula.fastestresponse.R
import com.madlula.fastestresponse.databinding.FragmentHomeBinding
import com.madlula.fastestresponse.utilities.Constants
import com.madlula.fastestresponse.utilities.Event


/**
 * Home fragment. Choose color for arrows and count down.
 */
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isAppInitialized().observe(viewLifecycleOwner, Event.Observer {
            if (it) {
                promptUserToPickColor()
            }
        })
        viewModel.goStartGame().observe(viewLifecycleOwner, Observer {
            goStartGame()
        })
        viewModel.getCountDown().observe(viewLifecycleOwner, Event.Observer {
            updateCountDown(it)
        })
        viewModel.init()
    }

    private fun promptUserToPickColor() {
        val builder = AlertDialog.Builder(requireContext())
        var pickedColor = 0
        builder.setTitle(R.string.tittle_color_dialog)
                .setSingleChoiceItems(R.array.color_list, 0
                ) { _, color ->
                    pickedColor = color
                }
                .setPositiveButton(getString(R.string.btn_ok)) { dialog, _ ->
                    viewModel.setChosenColor(pickedColor)
                    dialog.dismiss()
                }
        builder.show()

    }

    @SuppressLint("SetTextI18n")
    private fun updateCountDown(millisUntilFinished: Long){
        binding.txtCountDown.text = Constants.EMPTY_STRING + millisUntilFinished / 1000
    }
    private fun goStartGame(){
        binding.txtCountDown.text = getString(R.string.str_done)
        val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(viewModel.chosenColor.value!!)
        findNavController().navigate(action)
    }


}
