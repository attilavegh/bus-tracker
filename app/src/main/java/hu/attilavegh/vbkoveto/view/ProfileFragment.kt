package hu.attilavegh.vbkoveto.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso

import hu.attilavegh.vbkoveto.R
import hu.attilavegh.vbkoveto.LoginActivity
import hu.attilavegh.vbkoveto.TabbedActivity
import hu.attilavegh.vbkoveto.model.UserModel
import hu.attilavegh.vbkoveto.model.FragmentTagName

import kotlinx.android.synthetic.main.fragment_profile.view.*
import android.net.Uri
import android.support.v7.app.AlertDialog
import hu.attilavegh.vbkoveto.controller.ActivityTitleController
import hu.attilavegh.vbkoveto.controller.FragmentController
import hu.attilavegh.vbkoveto.controller.ToastController


class ProfileFragment: Fragment(),
    NotificationFragment.OnFragmentInteractionListener,
    View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var user: UserModel
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var titleController: ActivityTitleController
    private lateinit var toastController: ToastController
    private lateinit var fragmentController: FragmentController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        toastController = ToastController(context!!, resources)
        fragmentController = FragmentController(activity!!.supportFragmentManager)

        getParentContent()
        createGoogleAuthClient()
        fillProfileInfo(view)
        createSettingsItemListeners(view)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settings_notification -> onNotificationClick()
            R.id.settings_logout -> onLogoutClick()
            R.id.settings_website -> onWebsiteClick()
            R.id.settings_contact -> onContactClick()
        }
    }

    private fun createSettingsItemListeners(view: View) {
        val items: IntArray = intArrayOf(
            R.id.settings_notification,
            R.id.settings_logout,
            R.id.settings_website,
            R.id.settings_contact
        )

        for (item in items) {
            view.findViewById<LinearLayout>(item).setOnClickListener(this)
        }
    }

    override fun onNotificationInteraction() {}

    private fun onNotificationClick() {
        fragmentController.switchTo(NotificationFragment.newInstance(), FragmentTagName.NOTIFICATION.name)
        titleController.set(getString(R.string.notification))
    }

    private fun onLogoutClick() {
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(context, LoginActivity::class.java)
            this.startActivity(intent)

            finishTabbedActivity()
        }
    }

    private fun onWebsiteClick() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.veghbusz.hu"))
        startActivity(browserIntent)
    }

    private fun onContactClick() {
        showContactSelector()
    }

    interface OnFragmentInteractionListener {
        fun finishActivityAfterLogout()
    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    private fun finishTabbedActivity() = listener.let { it!!.finishActivityAfterLogout() }

    private fun createGoogleAuthClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        googleSignInClient = GoogleSignIn.getClient(context!!, gso)
    }

    private fun getParentContent() {
        val parentActivity = activity as TabbedActivity

        user = parentActivity.user
        titleController = parentActivity.titleController
    }

    private fun fillProfileInfo(view: View) {
        setProfilePicture(view)
        setName(view)
    }

    private fun setProfilePicture(view: View) {
        Picasso.with(context).load(user.imgUrl).fit().into(view.profile_picture)
    }

    private fun setName(view: View) {
        view.user_name.text = user.name
    }

    private fun showContactSelector() {
        val topics = arrayOf(
            resources.getString(R.string.contactBusiness),
            resources.getString(R.string.contactFeedback)
        )

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.contactTitle)
        builder.setItems(topics) { _, topic ->
            when (topic) {
                0 -> openEmailClient("test", topics[topic])
                1 -> openEmailClient("test2", topics[topic])
            }
        }

        builder.show()
    }

    private fun openEmailClient(recipient: String, subject: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

        try {
            startActivity(Intent.createChooser(emailIntent, subject))
        } catch (ex: android.content.ActivityNotFoundException) {
            toastController.create(R.string.contactNoEmailClient)
        }
    }
}
