import time

# Function to write current values to a file
def write_current_values(filename):
    # Step 0: Open the file in write mode to clear its contents (empty the file)
    with open(filename, 'w') as file:
        pass  # Opening the file in 'w' mode clears it

    # Step 1: Wait for 9 seconds before starting
    print("Waiting for 9 seconds...")
    time.sleep(13)

    # Step 2: Open the file again in write mode to start writing values
    with open(filename, 'w') as file:
        # Write values from 100 to 600 with a 0.25 second interval, increasing by 10
        current = 100
        while current <= 600:
            file.write(f"{current}\n")
            file.flush()  # Ensure data is written to the file immediately
            print(f"Writing {current} to file.")
            current += 10  # Increase by 10
            time.sleep(0.25)
        
        # After 4 seconds, increase values step by step to 2000, increasing by 20
        time.sleep(4)  # Pause for 4 seconds
        while current <= 2000:
            file.write(f"{current}\n")
            file.flush()  # Ensure data is written to the file immediately
            print(f"Writing {current} to file.")
            current += 20  # Increase by 20
            time.sleep(0.25)
        
        # Write values from 2000 to 3000 without delay, increasing by 50
        while current <= 3000:
            file.write(f"{current}\n")
            file.flush()  # Ensure data is written to the file immediately
            print(f"Writing {current} to file.")
            current += 50  # Increase by 50

# Call the function to write to the file
write_current_values("milliamp.txt")

