My strategy was to write unit tests to cover expected behaviour of the `Repl` class when given a mock input stream, mock output stream, and mock `BNFParser` instance to test the `Repl` class independently of the `BNFParser` class. I subclassed the `BNFParser` class to override the `isValidSentence` method to return mock success and failure responses to test the `Repl` class's canned responses to valid and invalid sentences.

I then wrote a separate suite of unit tests to cover the possible metasyntax of BNF rule definitions to interpret using ISP. I partitioned the input space into valid and invalid BNF rule definitions based on the presence of a optional symbols, repeatable symbols, alternative productions, numerical symbols, and nested brackets. I then wrote test cases to cover each of these partitions to test the `BNFParser` class's ability to interpret the metasyntax of BNF rule definitions. I then wrote methods for the `BNFParser` class to interpret the metasyntax of BNF rule definitions based on the partitions I had identified and pass the unit tests I had written. 

Once the `BNFParser` class passed all the unit tests, I integrated the `BNFParser` class with the `Repl` class and conducted parameterised testing to test the `Repl` class's ability to correctly interpret rules defined specifically for `Domolect 2.0`. I attempted to achieve production coverage of the `command` and `augmented_command` symbols separately by writing test cases for each of the alternative productions of the `command` and `augmented_command` symbols. The test cases for the `augmented_command` symbol were the same as those proposed in my response to Question 2 of the project spec. The test cases for the `command` symbol were:
- `living-room set incinerator to 6 9 0 K` (Optional location with thermal_device_command)
- `open curtains` (barrier_command)
- `turn laser-cannon on` (appliance_command)
- `turn brazier off` (lighting_command)

The test cases for the `augmented_command` symbol were:
- `kitchen set incinerator to 6 9 K when current-temperature less-than 6 9 K`
- `kitchen set incinerator to 6 9 K until current-temperature equal-to 6 9 K`
- `kitchen set incinerator to 6 9 K when 1 2 : 3 4 am until 0 7 : 5 6 pm`
- `kitchen set incinerator to 6 9 K when current-temperature greater-than 6 9 K until 0 9 : 0 8 am`

After reading the readme for the Linux parser provided on the CITS5501 website, I suspect my Java implementation is quite similar in logic as it also uses recursion to deal with nested brackets and alternative productions. 

