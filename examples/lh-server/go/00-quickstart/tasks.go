package main

func VerifyIdentity(fullName, email string, ssn int) string {
	return "Verification request accepted for " + fullName + " at " + email
}

func NotifyCustomerVerified(fullName, email string) string {
	return "Notified " + fullName + " at " + email + " that their identity was verified"
}

func NotifyCustomerNotVerified(fullName, email string) string {
	return "Notified " + fullName + " at " + email + " that their identity was not verified"
}
