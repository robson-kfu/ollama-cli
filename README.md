[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.robson-kfu/ollama-cli.svg?include_prereleases)](https://clojars.org/org.clojars.robson-kfu/ollama-cli)

# ollama/cli

`ollama/cli` is a Clojure library designed to simplify and abstract the interaction with the Ollama system. This library provides a high-level API to facilitate easy access to Ollama's functionalities, hiding the complexities involved in the direct use of the Ollama system. 

Whether you're building applications that depend on Ollama or scripting automated tasks, `ollama-cli` offers a convenient Clojure interface that enhances productivity and reduces boilerplate code.

## Features

- **Simple API**: Offers a straightforward and easy-to-use API for all Ollama operations.

## Getting Started

### Prerequisites

Before you can use `ollama/cli`, ensure you have Clojure and Leiningen installed on your machine:

- [Clojure](https://clojure.org/guides/getting_started)
- [Leiningen](https://leiningen.org/)

### Usage

Here is a basic example of how to use `ollama/cli` in your Clojure application:

```clojure
'(:require [ollama.cli.chat :as chat])

(def messages
  [{:role "user"
    :content "Why is the sky blue?"}])
(def model "phi3")
(def response (chat/chat! model messages))
(doseq [chunk response]
  (print (str chunk)))
```

## Documentation

Further documentation detailing all functions and their usage is available under the `doc` directory.

## Contributing

Contributions to `ollama-cli` are welcome and appreciated. Please refer to the CONTRIBUTING.md for guidelines on how to make contributions.

## License

`ollama-cli` is open source and is licensed under the MIT License. See the LICENSE file for more details.

## Support

If you encounter any issues or have questions, feel free to open an issue on the project's GitHub repository or contact the maintainers directly.

Thank you for using or considering `ollama-cli`!