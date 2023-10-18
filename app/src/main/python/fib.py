import json
from fibonacci import fibonacci

def pypi_pkg() -> 'list':
    """Run an imported package from PYPI.

    :rtype: list
    """
    return fibonacci(500)

def fibonacci_of(n) -> 'int':
    if not isinstance(n, int):
        raise TypeError("integer argument needed")

    if n in {0, 1}:  # Base case
        return n
    return fibonacci_of(n - 1) + fibonacci_of(n - 2)


def findings_print(prefix: str):
    """Serialize dict for Java, ajax like Flask communication to JavaScript.

    :param str prefix: custom string value for description of output
    :rtype: json_dict
    """
    fib_dct = {"prefix": prefix,
                "fibonacci_list": [fibonacci_of(n) for n in range(15)],
                "pypi_pkg_lst": pypi_pkg()}
    return json.dumps(fib_dct)