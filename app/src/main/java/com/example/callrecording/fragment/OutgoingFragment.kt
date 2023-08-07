package com.example.callrecording.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callrecording.MyApplication
import com.example.callrecording.adapter.CallHistoryAdapter
import com.example.callrecording.databinding.FragmentHomeBinding
import com.example.callrecording.databinding.FragmentOutgoingBinding
import com.example.callrecording.viewmodel.NotificationsViewModel
import kotlinx.coroutines.launch

class OutgoingFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var context : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/


        lifecycleScope.launch {

            MyApplication.repository.outgoingPhoneCallFlowList.collect{

                binding.containerNoCall.visibility=if(it.isEmpty())
                    View.VISIBLE
                else
                    View.GONE


                binding.rvCallLogs.visibility=if(it.isEmpty())
                    View.GONE
                else
                    View.VISIBLE


                binding.rvCallLogs.adapter = CallHistoryAdapter(it)
                binding.rvCallLogs.layoutManager = LinearLayoutManager(context)
                binding.rvCallLogs.setHasFixedSize(true)
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}