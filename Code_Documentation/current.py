import smbus
import time

# Define ADS1115 I2C address and registers
ADS1115_ADDRESS = 0x48  # I2C address of the ADS1115 ADC
ADS1115_CONVERSION_REGISTER = 0x00  # Register to read conversion results
ADS1115_CONFIG_REGISTER = 0x01  # Register to configure the ADC

# Initialize the I2C bus (create bus object)
bus = smbus.SMBus(1)  # Use I2C bus 1, which is standard on Raspberry Pi

def configure_ads1115(channel):
    """
    Configures the ADS1115 for single-ended mode, 16-bit resolution, 
    default gain, and selects the input channel.
    
    :param channel: ADC input channel to configure (0 for AIN0)
    """
    # Base configuration for single-ended mode, 16-bit, default gain
    CONFIG = 0xC383  # Single-shot mode, 128 SPS, ±4.096V range
    
    # Set the MUX to select the input channel
    if channel == 0:
        CONFIG |= 0x4000  # Select AIN0
    else:
        raise ValueError("Invalid channel. Choose 0 for AIN0.")

    # Write the configuration to the ADS1115 CONFIG_REGISTER
    bus.write_word_data(ADS1115_ADDRESS, ADS1115_CONFIG_REGISTER, CONFIG)

def read_ads1115():
    """
    Reads the conversion result from the ADS1115 ADC.

    :return: The ADC conversion result as a 16-bit integer.
    """
    # Read the conversion result from the CONVERSION_REGISTER
    result = bus.read_word_data(ADS1115_ADDRESS, ADS1115_CONVERSION_REGISTER)
    
    # Swap the byte order (from big-endian to little-endian)
    result = ((result & 0xFF00) >> 8) | ((result & 0x00FF) << 8)
    
    return result

def convert_adc_to_current(adc_value):
    """
    Converts the ADC reading to a current value in amperes using the ACS712 sensor.
    
    :param adc_value: The ADC reading as a 16-bit integer.
    :return: The converted current value in amperes.
    """
    # Conversion constants for ACS712 (185 mV/A sensitivity, 16-bit ADC)
    Vref = 4.096  # Reference voltage for ADS1115
    sensitivity = 0.185  # Sensitivity in V/A
    adc_resolution = 65535  # 16-bit resolution
    
    # Convert ADC value to voltage
    voltage = (adc_value * Vref) / adc_resolution
    
    # Convert voltage to current (ACS712)
    current = (voltage - Vref / 2) / sensitivity
    
    return current

if __name__ == "__main__":
    # Specify the channel to read from (0 for AIN0)
    channel = 0
    
    # Configure the ADS1115 to read from the specified channel
    configure_ads1115(channel)
    
    # Wait for the ADC conversion to complete
    time.sleep(0.1)
    
    # Read the ADC value
    adc_value = read_ads1115()
    
    # Convert the ADC reading to current
    current = convert_adc_to_current(adc_value)
    
    # Write the current reading to a file
    with open("/home/root/workspace/work_current/current.txt", "w") as f:
        f.write(f"{current:.3f} A\n")
    
    # Print the ADC value and converted current to the console
    print(f"ADC Value for channel {channel}: {adc_value}")
    print(f"Converted Current: {current:.3f} A")
