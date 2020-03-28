all: extension

extension:
	ant jar

clean:
	rm -f ADPassword.jar

.PHONY: all extension
