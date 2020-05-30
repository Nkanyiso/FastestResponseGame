package com.madlula.fastestresponse.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
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
import com.madlula.fastestresponse.viewModel.BaseViewModel
import com.madlula.fastestresponse.viewModel.Event


/**
 * Home fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: BaseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProviders.of(requireActivity()).get(BaseViewModel::class.java)
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
        viewModel.choosenColor.observe(requireActivity(), Observer {
            colorChoosen()
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
                    print("Choosen " + color.toString())
                    viewModel.choosenColor.value = pickedColor
                    dialog.dismiss()
                }))
        builder.show()
        builder.setCancelable(true)
    }

    fun colorChoosen() {
        countDown()

    }

    fun countDown() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.txtCountDown.text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                binding.txtCountDown.text = getString(R.string.str_done)
                findNavController().navigate(R.id.action_homeFragment_to_gameFragment)
            }
        }.start()

    }

}
