import smbus
import time

# Define ADS1115 I2C address and registers
ADS1115_ADDRESS = 0x48
ADS1115_CONVERSION_REGISTER = 0x00
ADS1115_CONFIG_REGISTER = 0x01

# Initialize the I2C bus (create bus object)
bus = smbus.SMBus(1)  # Use bus 1 for Raspberry Pi

def configure_ads1115(channel):
    # Base configuration for single-ended mode, 16-bit, default gain
    CONFIG = 0xC383  # Default settings (single-shot, 128 SPS, ±4.096V range)

    # Set MUX to the selected channel
    if channel == 0:
        CONFIG |= 0x4000  # AIN0
    elif channel == 1:
        CONFIG |= 0x5000  # AIN1
    elif channel == 2:
        CONFIG |= 0x6000  # AIN2
    elif channel == 3:
        CONFIG |= 0x7000  # AIN3
    else:
        raise ValueError("Invalid channel. Choose from 0, 1, 2, 3.")

    bus.write_word_data(ADS1115_ADDRESS, ADS1115_CONFIG_REGISTER, CONFIG)

def read_ads1115():
    # Read the conversion result
    result = bus.read_word_data(ADS1115_ADDRESS, ADS1115_CONVERSION_REGISTER)
    # Swap byte order
    result = ((result & 0xFF00) >> 8) | ((result & 0x00FF) << 8)
    return result

def convert_adc_to_temperature(adc_value):
    # Conversion constants
    min_adc = 15797
    max_adc = 55007
    min_temp = -5.0  # Minimum temperature in °C
    max_temp = 50.0  # Maximum temperature in °C
    
    # Convert the ADC value to temperature
    temperature = ((adc_value - min_adc) * (max_temp - min_temp) / (max_adc - min_adc)) + min_temp
    return round(temperature)  # Round to nearest whole number

def simulate_good_reading(raw_reading):
    # Define the old min and max values from your data
    old_min = -27
    old_max = 65
    
    # Define the new min and max values for the simulated range
    new_min = 0
    new_max = 100
    
    # Apply linear transformation
    if raw_reading < old_min:
        raw_reading = old_min
    elif raw_reading > old_max:
        raw_reading = old_max
    
    # Map the old range to the new range
    simulated_reading = ((raw_reading - old_min) / (old_max - old_min)) * (new_max - new_min) + new_min
    
    return round(simulated_reading)

if __name__ == "__main__":
    channel = 1  # Set channel 1
    previous_temperature = None  # Initialize variable to track previous temperature

    while True:
        configure_ads1115(channel)
        time.sleep(0.1)  # Wait for conversion to complete
        adc_value = read_ads1115()
        
        # Convert the ADC reading to temperature
        temperature = convert_adc_to_temperature(adc_value)
        
        # Simulate a good reading from the noisy data
        simulated_reading = simulate_good_reading(temperature)
        
        # Check if the simulated reading has changed
        if simulated_reading != previous_temperature:
            # Append the simulated reading to the file
            with open("/home/root/workspace/final_work/pot.txt", "a") as f:
                f.write(f"{simulated_reading}\n")
            
            # Print to the console
            print(f"ADC Value for channel {channel}: {adc_value}")
            print(f"Converted Temperature: {temperature}°C")
            print(f"Simulated Reading: {simulated_reading}")
            
            # Update the previous temperature
            previous_temperature = simulated_reading
        
        time.sleep(0.5)  # Wait for 100ms before the next reading
