package ro.marcu.licenta.api;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String temperature;
    private String icon;
    private String city;
    private String weatherType;
    private String tempFeeling;
    private String wind;
    private String humidity;
    private int condition;

    public static WeatherData fromJSON(JSONObject jsonObject) {

        try {

            int roundedValue;

            WeatherData weather = new WeatherData();
            weather.city = jsonObject.getString("name");
            weather.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weather.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weather.icon = updateWeatherIcon(weather.condition);

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            roundedValue = (int) Math.rint(tempResult);
            weather.temperature = Integer.toString(roundedValue);

            double tempFeelsResult = jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15;
            roundedValue = (int) Math.rint(tempFeelsResult);
            weather.tempFeeling = Integer.toString(roundedValue);

            int humidityValue = jsonObject.getJSONObject("main").getInt("humidity");
            weather.humidity = Integer.toString(humidityValue);

            double windValue = jsonObject.getJSONObject("wind").getDouble("speed");
            roundedValue = (int) Math.rint(windValue);
            weather.wind = Integer.toString(roundedValue);

            return weather;


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }


    }

    private static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition <= 300) {
            return "storm";
        } else if (condition >= 300 && condition <= 500) {
            return "light_rain";
        } else if (condition >= 500 && condition <= 600) {
            return "rain";
        } else if (condition >= 600 && condition <= 700) {
            return "snow1";
        } else if (condition >= 701 && condition <= 771) {
            return "foggy";
        } else if (condition >= 772 && condition < 800) {
            return "overcast";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy";
        } else if (condition >= 900 && condition < 902) {
            return "storm";
        } else if (condition == 903) {
            return "snow1";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition < 1000) {
            return "storm";
        }

        return "something";
    }

    public String getTemperature() {
        return temperature;
    }

    public String getTempFeeling() {
        return tempFeeling + "°C";
    }

    public String getWind() {
        return wind + " m/s";
    }

    public String getHumidity() {
        return humidity + "%";
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
