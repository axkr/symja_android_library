## FileHash

```
FileHash(file)
```

> computes an MD5 hash for the contents of the specified `file`. The FileHash function computes a cryptographic hash for the contents of a `file`. It is useful for verifying file integrity and detecting changes.  

```
FileHash(file, hash-type)
```

> computes a hash of the specified `hash-type` (e.g., "SHA-256") for the file.

### Examples

```
>> FileHash(StringToStream("Hello World"),"MD5") 
235328152096874191772633713977838157797
```

### Related terms 
[Hash](Hash.md) 