import smbus
import time

# Constants for the ADS1115
I2C_ADDRESS = 0x48  # Default I2C address for ADS1115
CONFIG_REG = 0x01
CONVERSION_REG = 0x00

# Config parameters for the ADS1115
CONFIG_OS_SINGLE = 0x8000  # Write: Set to start a single-conversion
CONFIG_MUX_AIN0 = 0x4000   # Input: AIN0 (P) and GND (N)
CONFIG_PGA_6_144V = 0x0000 # +/-6.144V range
CONFIG_MODE_SINGLE = 0x0100 # Single-shot mode
CONFIG_DR_128SPS = 0x0080  # 128 samples per second
CONFIG_CQUE_NONE = 0x0003  # Disable comparator and set alert pin to high-impedance

# Calculate configuration value
config = (CONFIG_OS_SINGLE | CONFIG_MUX_AIN0 | CONFIG_PGA_6_144V |
          CONFIG_MODE_SINGLE | CONFIG_DR_128SPS | CONFIG_CQUE_NONE)

def read_adc(channel):
    # Set up the I2C bus
    bus = smbus.SMBus(1)

    # Write config register to start a single conversion
    bus.write_i2c_block_data(I2C_ADDRESS, CONFIG_REG, [(config >> 8) & 0xFF, config & 0xFF])

    # Wait for the conversion to complete
    time.sleep(0.1)  # 100ms delay

    # Read the conversion result
    data = bus.read_i2c_block_data(I2C_ADDRESS, CONVERSION_REG, 2)
    
    # Convert the result to a signed 16-bit value
    raw_adc = data[0] << 8 | data[1]
    if raw_adc > 0x7FFF:
        raw_adc -= 0x10000
    
    return raw_adc

def calculate_current_mA(adc_value):
    # ACS712 calculation
    VREF = 3.3  # Reference voltage
    ADC_MAX = 32767.0  # 16-bit ADC resolution
    MV_PER_AMP = 185.0  # Sensitivity for ACS712 (in mV/A) for the 5A version

    # Convert ADC value to voltage
    voltage = (adc_value / ADC_MAX) * VREF
    current = (voltage - VREF / 2) / (MV_PER_AMP / 1000)
    return current * 1000  # Convert to milliamps

if __name__ == "__main__":
    output_file = "/home/root/workspace/final_work2/current.txt"
    
    try:
        while True:
            adc_value = read_adc(0)  # Reading from channel 0
            current_mA = calculate_current_mA(adc_value)
            current_mA = current_mA + 3360
            print(current_mA) 
            with open(output_file, "a") as file:
                file.write(f"{current_mA:.3f}\n")
            
            time.sleep(2)  # Update every second
    except KeyboardInterrupt:
        print("Script terminated.")
         
