
package com.example.mycalc2.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.mycalc2.R
import com.example.mycalc2.databinding.DialogThemeSelectionBinding
import com.example.mycalc2.utils.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThemeBottomSheetDialog(private val rootView: View) : BottomSheetDialogFragment() {

    private var _binding: DialogThemeSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        _binding = DialogThemeSelectionBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.colorRed.setOnClickListener { selectTheme(Color.WHITE) }
        binding.colorBlue.setOnClickListener { selectTheme(Color.BLUE) }
        binding.colorGreen.setOnClickListener { selectTheme(Color.GREEN) }
        binding.colorDefault.setOnClickListener { selectTheme(Color.parseColor("#FF6200EE")) }

        return dialog
    }

    private fun selectTheme(color: Int) {
        ThemeManager.saveThemeColor(requireContext(), color)  // Сохраняем цвет в Firebase
        ThemeManager.applyTheme(rootView, color)  // Применяем цвет
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
