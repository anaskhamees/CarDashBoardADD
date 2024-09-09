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

if __name__ == "__main__":
    # Specify the channel to read from (0 for AIN0)
    channel = 0
    
    # Configure the ADS1115 to read from the specified channel
    configure_ads1115(channel)
    
    # Wait for the ADC conversion to complete
    time.sleep(0.1)
    
    # Read the ADC value
    value = read_ads1115()
    
    # Write the ADC value to a file
    with open("/home/root/workspace/work_current/current.txt", "w") as f:
        f.write(f"{value}")
    
    # Print the ADC value to the console
    print(f"ADC Value for channel {channel}: {value}")
