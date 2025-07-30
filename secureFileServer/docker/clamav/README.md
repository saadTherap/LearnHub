# ClamAV Setup Instructions

Before starting the ClamAV Docker container, ensure the log directory exists and is writable by your user:

```bash
sudo mkdir -p /var/log/secure-file-storage
sudo chmod 777 /var/log/secure-file-storage
```

Then start ClamAV with:

```bash
.clamav/clamav-start.sh
```

Then to stop ClamAV:
```bash
.clamav/clamav-stop.sh
```


To inspect ClamAV logs manually:
```bash
cat /var/log/secure-file-storage/clamav.log
```
Or follow logs in real-time:

```bash
tail -f /var/log/secure-file-storage/clamav.log
```
