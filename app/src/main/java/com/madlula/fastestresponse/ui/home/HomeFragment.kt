package com.madlula.fastestresponse.ui.home

import android.content.DialogInterface
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
import com.madlula.fastestresponse.ui.HomeFragmentDirections
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
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isAppInitialized().observe(requireActivity(), Event.Observer {
            if (it) {
                proptUserPickeColor()
            }
        })
        viewModel.isCountDownFinished().observe(requireActivity(), Observer {
            finishedCountDown()
        })
        viewModel.getcountDown().observe(requireActivity(), Event.Observer {
            updateCountDown(it)
        })
        viewModel.init()


    }

    override fun onResume() {
        super.onResume()
    }

    fun proptUserPickeColor() {
        val builder = AlertDialog.Builder(requireContext())
        var pickedColor = 0
        builder.setTitle(R.string.tittle_color_dialog)
                .setSingleChoiceItems(R.array.color_list, 0,
                        DialogInterface.OnClickListener { _, color ->
                            // dialog.cancel()
                            pickedColor = color


                        })
                .setPositiveButton(getString(R.string.btn_ok), DialogInterface.OnClickListener({ dialog, color ->
                    viewModel.setChosenColor(pickedColor)
                    dialog.dismiss()
                }))
        builder.show()
        builder.setCancelable(true)
    }

    fun updateCountDown(millisUntilFinished: Long){
        binding.txtCountDown.text = "" + millisUntilFinished / 1000
    }
    fun finishedCountDown(){
        binding.txtCountDown.text = getString(R.string.str_done)
        val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(viewModel.chosenColor.value!!)
        findNavController().navigate(action)
    }


}
