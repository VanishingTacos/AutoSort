# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.1.x   | :white_check_mark: |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

**Please do NOT open public issues for security vulnerabilities.**

Security vulnerabilities should be reported privately to ensure responsible disclosure and protect users.

### How to Report

1. **Email**: Send detailed reports to `me@vanishingtacos.com`
2. **GitHub Security Advisory**: Use the "Report a vulnerability" button on the Security tab
3. **Private Issue**: Create a private issue with the `[SECURITY]` prefix

### What to Include

Please provide the following information when reporting:

- **Description**: Clear description of the vulnerability
- **Steps to Reproduce**: Detailed steps to reproduce the issue
- **Impact Assessment**: Potential impact on users/servers
- **Environment**: Minecraft version, server type, plugin version
- **Proof of Concept**: Code or commands that demonstrate the issue
- **Suggested Fix**: Any ideas for mitigation (optional)

### Vulnerability Categories

This policy covers security issues in:

- **Authentication & Authorization**: Permission bypasses, privilege escalation
- **Data Validation**: Input validation, injection attacks
- **Resource Exhaustion**: DoS vulnerabilities, memory leaks
- **Information Disclosure**: Sensitive data exposure
- **Database Security**: SQL injection, credential exposure
- **Rate Limiting**: Bypass of protection mechanisms

## Disclosure Timeline

We are committed to responsible disclosure and will follow this timeline:

| Phase | Timeline | Action |
|-------|----------|--------|
| **Initial Response** | 48 hours | Acknowledge receipt of report |
| **Assessment** | 7 days | Confirm vulnerability and assess severity |
| **Fix Development** | 1-30 days | Develop and test security fix |
| **Coordination** | Ongoing | Work with reporter on disclosure |
| **Public Release** | Coordinated | Release fix and security advisory |

**Note**: Timeline may vary based on vulnerability severity and complexity.

## Severity Levels

| Level | Description | Response Time |
|-------|-------------|---------------|
| **Critical** | Remote code execution, data corruption | 24-48 hours |
| **High** | Privilege escalation, data exposure | 3-7 days |
| **Medium** | DoS, information disclosure | 7-14 days |
| **Low** | Minor issues, edge cases | 14-30 days |

## Security Features

InventoryWizard includes several security measures:

- **Rate Limiting**: Prevents resource exhaustion attacks
- **Input Validation**: Sanitizes all user inputs
- **Permission Checks**: Server-side permission validation
- **Database Security**: Encrypted credentials, prepared statements
- **Error Handling**: Secure error messages without information disclosure

## Security History

- **v1.1.0**: Removed H2 console access, enhanced rate limiting
- **v1.0.0**: Initial security audit and implementation

## Contact Information

- **Security Email**: me@vanishingtacos.com
- **GitHub Issues**: Use private issues for security reports
- **Discord**: Join our community for general support (not security issues)

## Acknowledgments

We appreciate security researchers who follow responsible disclosure practices. Contributors will be acknowledged in security advisories unless they prefer to remain anonymous.

## Legal

By reporting a vulnerability, you agree to:
- Not publicly disclose the issue until we've had time to address it
- Not use the vulnerability for malicious purposes
- Provide reasonable time for us to develop and test fixes

---

**Thank you for helping keep InventoryWizard secure!** 🛡️ 
