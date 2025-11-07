package com.example.weather

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    //appid = a462655c9b2a2dbd5a5fd6ab4bff6f45
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherdata(city = "Karachi")
        searchCity()
     }

     private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherdata(query)
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherdata(city: String) {
       val retrofit = Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiInterface::class.java)
        val response = retrofit.getweatherdata(city, appid = "a462655c9b2a2dbd5a5fd6ab4bff6f45", units = "metric")
        response.enqueue(object :Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    binding.temp.text = "$temperature"
                    binding.humidity.text = "$humidity %"
                    binding.weather.text= condition
                    binding.sealevel.text = "$sealevel hPa"
                    binding.windspeed.text= "$windspeed m/s"
                    binding.sunrise.text= "${time(sunrise)}"
                    binding.sunset.text= "${time(sunset)}"
                    binding.currentTime.text = getCurrentTime()

                    binding.conditon.text = condition
                    binding.day.text =dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.city.text = "$city"


                    change_img_accroding_to_weatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun change_img_accroding_to_weatherCondition(condition: String) {
        when(condition) {
           "Clouds", "Haze", "Fog" , "Mist", "Smoke", "Mostly Cloudy", "Cloudy", "Partly Cloudy", "Overcast" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Heavy Rain", "Showers", "Drizzle", "Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Sunny", "Clear", "Mostly Sunny", "Sunrise", "Sunset" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Snow", "Heavy Snow", "Bizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Thunderstorm", "Lightning", "Storm" ->{
                binding.root.setBackgroundResource(R.drawable.storm)
                binding.lottieAnimationView.setAnimation(R.raw.thunderstrom)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long): String {

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Example: "03:45 PM"
        return sdf.format(Date()) // Current time
    }

}
