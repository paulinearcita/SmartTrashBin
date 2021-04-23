package com.example.mqttkotlinsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_client.*
import org.eclipse.paho.client.mqttv3.*
import java.util.*

class ClientFragment : Fragment() {
    private lateinit var mqttClient : MQTTClient

    var dayOfWeek = 1
    var notifsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mqttClient.isConnected()) {
                    // Disconnect from MQTT Broker
                    mqttClient.disconnect(object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(this.javaClass.name, "Disconnected")

                            Toast.makeText(context, "MQTT Disconnection success", Toast.LENGTH_SHORT).show()

                            // Disconnection success, come back to Connect Fragment
                            findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(this.javaClass.name, "Failed to disconnect")
                        }
                    })
                } else {
                    Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
                }
            }
        })



    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client, container, false)

    }

    // NEED
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed by ConnectFragment
        val serverURI   = arguments?.getString(MQTT_SERVER_URI_KEY)
        val clientId    = arguments?.getString(MQTT_CLIENT_ID_KEY)
        val username    = arguments?.getString(MQTT_USERNAME_KEY)
        val pwd         = arguments?.getString(MQTT_PWD_KEY)

        val spinner: Spinner = trashDaySpinner


        ArrayAdapter.createFromResource(
                this.requireActivity(),
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1)
            trashDaySpinner.adapter = adapter
        }



        trashDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

                if(trashDaySpinner.selectedItem.toString() == "Monday")
                    dayOfWeek = 1
                else if(trashDaySpinner.selectedItem.toString() == "Tuesday")
                    dayOfWeek = 2
                else if(trashDaySpinner.selectedItem.toString() == "Wednesday")
                    dayOfWeek = 3
                else if(trashDaySpinner.selectedItem.toString() == "Thursday")
                    dayOfWeek = 4
                else if(trashDaySpinner.selectedItem.toString() == "Friday")
                    dayOfWeek = 5
                else if(trashDaySpinner.selectedItem.toString() == "Saturday")
                    dayOfWeek = 6
                else if(trashDaySpinner.selectedItem.toString() == "Sunday")
                    dayOfWeek = 7


            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                dayOfWeek = 1
            }

        }

        notifSwitch.isChecked = notifsEnabled

        notifSwitch.setOnClickListener {

            notifsEnabled = notifSwitch.isChecked

        }



        // Check if passed arguments are valid
        if (    serverURI   != null    &&
                clientId    != null    &&
                username    != null    &&
                pwd         != null        ) {
            // Open MQTT Broker communication
            mqttClient = MQTTClient(context, serverURI, clientId)

            // Connect and login to MQTT Broker
            mqttClient.connect( username,
                    pwd,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(this.javaClass.name, "Connection success")

                            Toast.makeText(context, "MQTT Connection success", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")

                            Toast.makeText(context, "MQTT Connection fails: ${exception.toString()}", Toast.LENGTH_SHORT).show()

                            // Come back to Connect Fragment
                            findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                        }
                    },
                    object : MqttCallback {
                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            val msg = "Receive message: ${message.toString()} from topic: $topic"
                            Log.d(this.javaClass.name, msg)

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun connectionLost(cause: Throwable?) {
                            Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.d(this.javaClass.name, "Delivery complete")
                        }
                    })
        } else {
            // Arguments are not valid, come back to Connect Fragment
            findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
        }

        view.findViewById<Button>(R.id.button_prefill_client).setOnClickListener {
            // Set default values in edit texts
            view.findViewById<EditText>(R.id.edittext_pubtopic).setText(MQTT_TEST_TOPIC)
            //view.findViewById<EditText>(R.id.edittext_pubmsg).setText(MQTT_TEST_MSG)
            //view.findViewById<EditText>(R.id.edittext_subtopic).setText(MQTT_TEST_TOPIC)
        }

        view.findViewById<Button>(R.id.button_clean_client).setOnClickListener {
            // Clean values in edit texts
            view.findViewById<EditText>(R.id.edittext_pubtopic).setText("")
            //view.findViewById<EditText>(R.id.edittext_pubmsg).setText("")
            //view.findViewById<EditText>(R.id.edittext_subtopic).setText("")
        }

        view.findViewById<Button>(R.id.button_disconnect).setOnClickListener {
            if (mqttClient.isConnected()) {
                // Disconnect from MQTT Broker
                mqttClient.disconnect(object : IMqttActionListener {
                                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                                Log.d(this.javaClass.name, "Disconnected")

                                                Toast.makeText(context, "MQTT Disconnection success", Toast.LENGTH_SHORT).show()

                                                // Disconnection success, come back to Connect Fragment
                                                findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                                            }

                                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                                Log.d(this.javaClass.name, "Failed to disconnect")
                                            }
                                        })
            } else {
                Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
            }
        }

        view.findViewById<Button>(R.id.btnLock).setOnClickListener {
            val topic   = view.findViewById<EditText>(R.id.edittext_pubtopic).text.toString()
            val message = "a"

            if (mqttClient.isConnected()) {
                mqttClient.publish(topic,
                    message,
                    1,
                    false,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg ="Publish message: $message to topic: $topic"
                            Log.d(this.javaClass.name, msg)

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(this.javaClass.name, "Failed to publish message to topic")
                        }
                    })
            } else {
                Log.d(this.javaClass.name, "Impossible to publish, no server connected")
            }
        }

        testButton.setOnClickListener {
            var s = "Monday"
            when (dayOfWeek) {
                1 -> s = "Monday"
                2 -> s = "Tuesday"
                3 -> s = "Wednesday"
                4 -> s = "Thursday"
                5 -> s = "Friday"
                6 -> s = "Saturday"
                7 -> s = "Sunday"
            }


            val channel_id = "id1"
            createChannel("id1", "Channel1")
            val trashDayNofifID = 1
            val d = Date()
            @Suppress("DEPRECATION") val day = d.day
            if(day == dayOfWeek && notifsEnabled) //replace with saved setting
            {
                var builder = NotificationCompat.Builder(this.requireActivity(), channel_id)
                        .setSmallIcon(R.drawable.knight)
                        .setContentTitle("It is $s. Trash Day!")
                        .setContentText(Context.NOTIFICATION_SERVICE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(this.requireActivity())) {
                    // notificationId is a unique int for each notification that you must define
                    notify(trashDayNofifID, builder.build())
                }

            }

        }

        view.findViewById<Button>(R.id.btnUnlock).setOnClickListener {
            val topic   = view.findViewById<EditText>(R.id.edittext_pubtopic).text.toString()
            val message = "b"

            if (mqttClient.isConnected()) {
                mqttClient.publish(topic,
                    message,
                    1,
                    false,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg ="Publish message: $message to topic: $topic"
                            Log.d(this.javaClass.name, msg)

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(this.javaClass.name, "Failed to publish message to topic")
                        }
                    })
            } else {
                Log.d(this.javaClass.name, "Impossible to publish, no server connected")
            }
        }


        // NEED -- done
//        view.findViewById<Button>(R.id.button_publish).setOnClickListener {
//            val topic   = view.findViewById<EditText>(R.id.edittext_pubtopic).text.toString()
//            val message = view.findViewById<EditText>(R.id.edittext_pubmsg).text.toString()
//
//            if (mqttClient.isConnected()) {
//                mqttClient.publish(topic,
//                                    message,
//                                    1,
//                                    false,
//                                    object : IMqttActionListener {
//                                        override fun onSuccess(asyncActionToken: IMqttToken?) {
//                                            val msg ="Publish message: $message to topic: $topic"
//                                            Log.d(this.javaClass.name, msg)
//
//                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                                        }
//
//                                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                                            Log.d(this.javaClass.name, "Failed to publish message to topic")
//                                        }
//                                    })
//            } else {
//                Log.d(this.javaClass.name, "Impossible to publish, no server connected")
//            }
//        }

        // START Checking HERE ---------
//        view.findViewById<Button>(R.id.button_subscribe).setOnClickListener {
//            val topic   = view.findViewById<EditText>(R.id.edittext_subtopic).text.toString()
//
//            if (mqttClient.isConnected()) {
//                mqttClient.subscribe(topic,
//                        1,
//                        object : IMqttActionListener {
//                            override fun onSuccess(asyncActionToken: IMqttToken?) {
//                                val msg = "Subscribed to: $topic"
//                                Log.d(this.javaClass.name, msg)
//
//                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                            }
//
//                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                                Log.d(this.javaClass.name, "Failed to subscribe: $topic")
//                            }
//                        })
//            } else {
//                Log.d(this.javaClass.name, "Impossible to subscribe, no server connected")
//            }
//        }

//        view.findViewById<Button>(R.id.button_unsubscribe).setOnClickListener {
//            val topic   = view.findViewById<EditText>(R.id.edittext_subtopic).text.toString()
//
//            if (mqttClient.isConnected()) {
//                mqttClient.unsubscribe( topic,
//                        object : IMqttActionListener {
//                            override fun onSuccess(asyncActionToken: IMqttToken?) {
//                                val msg = "Unsubscribed to: $topic"
//                                Log.d(this.javaClass.name, msg)
//
//                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                            }
//
//                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                                Log.d(this.javaClass.name, "Failed to unsubscribe: $topic")
//                            }
//                        })
//            } else {
//                Log.d(this.javaClass.name, "Impossible to unsubscribe, no server connected")
//            }
//        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText

            val notificationManager = requireActivity().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)


        }
        // TODO: Step 1.6 END create a channel
    }


}