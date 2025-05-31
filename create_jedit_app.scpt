tell application "Finder"
	set scriptPath to POSIX file "/Users/rhysbeynon/j3D/launch_jedit.sh" as string
	set appPath to POSIX file "/Users/rhysbeynon/Applications/jEDIT.app" as string
	
	-- Create the directory if it doesn't exist
	do shell script "mkdir -p ~/Applications"
	
	-- Create the AppleScript application
	set appContent to "tell application \"Terminal\"
	do script \"" & scriptPath & "\"
end tell"
	
	-- Write the script to a temporary file
	set tempFile to POSIX path of (path to temporary items from user domain) & "jedit_launcher.scpt"
	do shell script "echo " & quoted form of appContent & " > " & quoted form of tempFile
	
	-- Compile the script to an application
	do shell script "osacompile -o ~/Applications/jEDIT.app " & quoted form of tempFile
	
	-- Clean up the temporary file
	do shell script "rm " & quoted form of tempFile
	
	-- Show confirmation
	display dialog "jEDIT launcher has been created in ~/Applications" buttons {"OK"} default button "OK"
end tell
