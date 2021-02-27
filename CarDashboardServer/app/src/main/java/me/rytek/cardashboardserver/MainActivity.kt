package me.rytek.cardashboardserver

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import me.rytek.cardashboardserver.interfaces.bluetooth.BluetoothChatService
import me.rytek.cardashboardserver.interfaces.bluetooth.BluetoothConstants
import me.rytek.cardashboardserver.viewmodel.BluetoothViewModel


class MainActivity : AppCompatActivity() {
    private val TAG = "Car Dashboard Server"

    // Intent request codes
    private var REQUEST_CONNECT_DEVICE_SECURE = 1
    private var REQUEST_CONNECT_DEVICE_INSECURE = 2
    private var REQUEST_ENABLE_BT = 3

    /**
     * String buffer for outgoing messages
     */
    private var mOutStringBuffer: StringBuffer = StringBuffer()

    /**
     * Local Bluetooth adapter
     */
    private var mBluetoothAdapter: BluetoothAdapter? = null

    /**
     * Member object for the chat services
     */
    private var mChatService: BluetoothChatService? = null

    private val model: BluetoothViewModel = BluetoothViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            finish()
        }
        
        setContent {
//            CarDashboardServerTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
//            }
//            CarDashboardServerTheme {
//                // A surface container using the 'background' color from the theme
//                MainScreen()
//            }
            MainScreen(model)

        }
    }

    override fun onStart() {
        super.onStart()
        model.addMessage("Started");
        Log.d(TAG, "Started");
        if (mBluetoothAdapter == null) {
            return
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatService != null) {
            mChatService!!.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        model.addMessage("Resumed");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService!!.state == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService!!.start()
            }
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private fun setupChat() {
        Log.d(TAG, "setupChat()")

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = BluetoothChatService(this@MainActivity, mHandler)

//        // Initialize the array adapter for the conversation thread
//        val activity = this;
//        mConversationArrayAdapter = ArrayAdapter(activity, R.layout.message)
//        mConversationView!!.adapter = mConversationArrayAdapter
//
//        // Initialize the compose field with a listener for the return key
//        mOutEditText!!.setOnEditorActionListener(mWriteListener)
//
//        // Initialize the send button with a listener that for click events
//        mSendButton!!.setOnClickListener { // Send a message using content of the edit text widget
//            val view: View = getView()
//            if (null != view) {
//                val textView = view.findViewById<TextView>(R.id.edit_text_out)
//                val message = textView.text.toString()
//                sendMessage(message)
//            }
//        }


    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private fun ensureDiscoverable() {
        if (mBluetoothAdapter!!.scanMode !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
        ) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)
        }
    }



    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val activity: Activity = this@MainActivity;
            when (msg.what) {
                BluetoothConstants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        model.setStatus(
                            getString(
                                R.string.title_connected_to,
                                model.connectedDeviceName.value
                            )
                        )
                        model.clearMessages()
                    }
                    BluetoothChatService.STATE_CONNECTING -> model.setStatus(getString(R.string.title_connecting))
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE ->
                        model.setStatus(getString(R.string.title_not_connected))
                }
                BluetoothConstants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    model.setMessages(model.messages.value!!.plus("Me:  $writeMessage"))
                }
                BluetoothConstants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    model.setMessages(model.messages.value!!.plus("${model.connectedDeviceName.value}:  $readMessage"))
                }
                BluetoothConstants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    model.setConnectedDeviceName(
                        msg.getData().getString(BluetoothConstants.DEVICE_NAME)!!
                    )
                    Toast.makeText(
                        activity, "Connected to "
                                + model.connectedDeviceName.value, Toast.LENGTH_SHORT
                    ).show()
                }
                BluetoothConstants.MESSAGE_TOAST ->
                    Toast.makeText(
                        activity, msg.getData().getString(BluetoothConstants.TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: BluetoothViewModel
) {
    val status: String? by viewModel.status.observeAsState("")
    val messages: List<String> by viewModel.messages.observeAsState(listOf())
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column() {
                    Text(text = "Car Dashboard Server")
                    if (!status.isNullOrEmpty()) {
                        Text(text = status!!, style = MaterialTheme.typography.subtitle1)
                    }
                }
            })
        }
    ) {
        Column {
            Greeting(name = "Ryan")
            LazyColumn {
                items(messages.size) {
                    Text(messages[it])
                }
            }
        }
    }
}

@Preview
@Composable
fun MainPreview() {
    val model = BluetoothViewModel()
    model.setStatus("Preview Status")
    model.setConnectedDeviceName("Galaxy S10")
    model.setMessages(listOf("hello", "my", "name", "is", "ryan"))
    MainScreen(viewModel = model)
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
