package com.wildraion.taskmanager.fragments.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.wildraion.taskmanager.databinding.FragmentWeatherBinding
import com.wildraion.taskmanager.model.City
import com.wildraion.taskmanager.model.Weather
import com.wildraion.taskmanager.util.Utils
import com.wildraion.taskmanager.viewmodel.CityViewModel
import com.wildraion.taskmanager.weather.WeatherModel
import com.wildraion.taskmanager.weather.remote.APIService
import com.wildraion.taskmanager.weather.remote.RetroClass
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class WeatherFragment : Fragment() {

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var handler: Handler
    private lateinit var disposable: Disposable
    private lateinit var apiService: APIService
    private lateinit var weatherModel: Single<WeatherModel>
    private lateinit var mCityViewModel: CityViewModel
    private lateinit var citiesList: List<City>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        handler = Handler((context as Activity).mainLooper)
        mCityViewModel = ViewModelProvider(this)[CityViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.weatherCityAc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                binding.weatherProgressbar.visibility = View.VISIBLE
                binding.weatherContentContainer.visibility = View.GONE
                searchData(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.weatherCityAc.onItemClickListener = OnItemClickListener { _, _, _, id ->
            val city = citiesList[id.toInt()]
            loadWeather(city.lat.toString(), city.lon.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLocation()
    }

    private fun loadWeather(lat: String, lon: String){
        apiService = RetroClass.getApiService()
        weatherModel = apiService.getWeatherModel(lat, lon,
            "69804d11c802801608e08fe1f8144146","metric")
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())

        weatherModel.subscribe(object : SingleObserver<WeatherModel> {
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            @SuppressLint("SetTextI18n", "SimpleDateFormat")
            override fun onSuccess(weatherModel: WeatherModel) {

                handler.post {

                    // Recycler View configs
                    val weatherList: MutableList<Weather> = mutableListOf()
                    for(w in weatherModel.list){
                        val time = w.dt_txt.split(' ')[1].split(":")

                        weatherList.add(Weather("${time[0]}:${time[1]}",
                            "${w.main.temp.toInt()}°C",
                            "ic_${w.weather[0].icon}"))
                    }
                    val adapter = WeatherAdapter()
                    val recyclerView = binding.weatherRecycler
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(context,
                        RecyclerView.HORIZONTAL, false)
                    adapter.setData(weatherList)

                    val currentWeather = weatherModel.list[0]

                    // Main weather description
                    binding.weatherTempTv.text = "${currentWeather.main.temp.toInt()}°C"
                    binding.weatherCityTv.text = weatherModel.city.name
                    binding.weatherFeelsTempTv.text = "${currentWeather.main.feels_like.toInt()}°C"
                    binding.weatherDescriptionTv.text = "${currentWeather.weather[0].main}, " +
                            "${currentWeather.main.temp_max.toInt()}°C/" +
                            "${currentWeather.main.temp_min.toInt()}°C"

                    // Main icon
                    binding.weatherMainIcon.setImageResource(Utils.getImageId(context!!,
                        "ic_${currentWeather.weather[0].icon}"))

                    // Other weather info
                    binding.weatherWind.text = "${currentWeather.wind.speed} m/s"
                    binding.weatherHumidity.text = "${currentWeather.main.humidity}%"
                    binding.weatherCloudiness.text = "${currentWeather.clouds.all}%"
                    binding.weatherTemp.text = "${currentWeather.main.temp}°C"
                    binding.weatherPressure.text = "${currentWeather.main.pressure} hPa"

                    val visibility = currentWeather.visibility
                    binding.weatherVisibility.text = if(visibility >= 1000) "${(visibility/1000).toInt()} km"
                    else "$visibility m"


                    // Hide progress bar
                    binding.weatherProgressbar.visibility = View.GONE
                    binding.weatherContentContainer.visibility = View.VISIBLE
                }
            }

            override fun onError(e: Throwable) {
                Log.e("weatherErrorCustom", e.message.toString())
            }
        })
    }

    private fun searchData(query: String) {
        val searchQuery = "%$query%"

        mCityViewModel.searchData(searchQuery).observe(this) { cities ->
            citiesList = cities
            binding.weatherCityAc.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cities.map { "${it.name}, ${it.country}" }
            ))
        }
    }

    private fun fetchLocation() {
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if(it != null){
                loadWeather(it.latitude.toString(), it.longitude.toString())
            }
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation()
        } else {
            Toast.makeText(requireContext(), "To display the weather for your current location, please confirm access for the location",
                Toast.LENGTH_LONG)
        }
    }
}