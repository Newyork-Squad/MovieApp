package com.karrar.movieapp.ui.profile.editProfile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karrar.movieapp.R
import com.karrar.movieapp.ui.profile.ProfileViewModel

private const val EDIT_PROFILE_URL = "https://www.themoviedb.org/settings/profile"
private const val LOGIN_URL = "https://www.themoviedb.org/login"

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }


        webView = view.findViewById(R.id.webView)
        progressBar = view.findViewById(R.id.progressBar)

        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val clickedUrl = request?.url?.toString() ?: return false
                val isAllowed = clickedUrl.startsWith(EDIT_PROFILE_URL) || clickedUrl.startsWith(LOGIN_URL)
                return !isAllowed
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@EditProfileFragment.filePathCallback = filePathCallback
                return true
            }
        }

        progressBar.visibility = View.VISIBLE
        webView.loadUrl(EDIT_PROFILE_URL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileViewModel.getData()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE

    }
}
