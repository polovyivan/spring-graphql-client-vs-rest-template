package com.polovyi.ivan.client;

import com.polovyi.ivan.dto.CreateCustomerRequest;
import com.polovyi.ivan.dto.CustomerResponse;
import com.polovyi.ivan.dto.PartiallyUpdateCustomerRequest;
import com.polovyi.ivan.dto.UpdateCustomerRequest;
import graphql.kickstart.spring.webclient.boot.GraphQLRequest;
import graphql.kickstart.spring.webclient.boot.GraphQLResponse;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GraphQLClient {

    private final GraphQLWebClient graphQLWebClient;

    public List<CustomerResponse> getAllCustomers() {
        log.info("[GraphQLClient] Calling getAllCustomers query...");
        String query = """
                query {
                    getAllCustomers {
                                id
                                fullName
                                phoneNumber
                                address
                                createdAt    
                                }
                }
                """;
        GraphQLRequest request = GraphQLRequest.builder().query(query).build();
        GraphQLResponse response = graphQLWebClient.post(request).block();
        return response.getList("getAllCustomers", CustomerResponse.class);
    }

    public List<CustomerResponse> getCustomersWithFilters(String fullName, String phoneNumber, LocalDate createdAt) {
        log.info("[GraphQLClient] Calling getAllCustomersWithFilters query...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.computeIfAbsent("fullName", value -> fullName);
        varMap.computeIfAbsent("phoneNumber", value -> phoneNumber);
        varMap.computeIfAbsent("createdAt", value -> createdAt);

        String query = """
                query ($fullName : String
                       $phoneNumber : String
                       $createdAt : Date ){
                       getAllCustomersWithFilters(fullName: $fullName
                                                  phoneNumber : $phoneNumber
                                                  createdAt : $createdAt) {
                                                                            id
                                                                            fullName
                                                                            phoneNumber
                                                                            address
                                                                            createdAt
                                                                          }
                }
                """;
        GraphQLRequest request = GraphQLRequest.builder().query(query).variables(varMap).build();
        GraphQLResponse response = graphQLWebClient.post(request).block();
        return response.getList("getAllCustomersWithFilters", CustomerResponse.class);
    }

    public String createCustomer(CreateCustomerRequest createCustomerRequest) {
        log.info("[GraphQLClient] Calling create customer mutation...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("$createCustomerRequest", createCustomerRequest);
        String mutation = """
                mutation ($createCustomerRequest : CreateCustomerRequest) {
                    createCustomer (createCustomerRequest : $createCustomerRequest)
                    {
                        id
                        fullName
                        phoneNumber
                        address
                        createdAt
                    }
                }
                }
                """;
        GraphQLRequest request = GraphQLRequest.builder().query(mutation).variables(varMap).build();
        GraphQLResponse response = graphQLWebClient.post(request).block();
        return response.get("createCustomer", CustomerResponse.class).getId();
    }

    // Using file instead of mutation string
    public void updateCustomer(String customerId, UpdateCustomerRequest updateCustomerRequest) {
        log.info("[GraphQLClient] Calling update customer mutation...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("customerId", customerId);
        varMap.put("updateCustomerRequest", updateCustomerRequest);

        GraphQLRequest request = GraphQLRequest.builder().resource("graphq-web-client-resource/update-customer.graphql")
                .variables(varMap).build();
        GraphQLResponse response = graphQLWebClient.post(request).block();
        response.get("updateCustomer", Void.class);
    }

    public void partiallyUpdateCustomer(String customerId,
            PartiallyUpdateCustomerRequest partiallyUpdateCustomerRequest) {
        log.info("[GraphQLClient] Calling partially update customer mutation...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("customerId", customerId);
        varMap.put("partiallyUpdateCustomerRequest", partiallyUpdateCustomerRequest);

        String mutation = """
                mutation ($partiallyUpdateCustomerRequest : PartiallyUpdateCustomerRequest
                           $customerId : String) {
                         partiallyUpdateCustomer (
                            customerId : $customerId
                            partiallyUpdateCustomerRequest : $partiallyUpdateCustomerRequest)
                                                  {
                                                    id
                                                    fullName
                                                    phoneNumber
                                                    address
                                                    createdAt
                                                  }
                }
                                """;
        GraphQLRequest request = GraphQLRequest.builder().query(mutation).variables(varMap).build();
        GraphQLResponse response = graphQLWebClient.post(request).block();
        response.get("partiallyUpdateCustomer", Void.class);
    }

    public void deleteCustomer(String customerId) {
        log.info("[GraphQLClient] Calling delete customer mutation...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("customerId", customerId);

        String mutation = """
                mutation ( $customerId : String) {
                         deleteCustomer (
                                         customerId : $customerId)
                                                  }
                   """;
        GraphQLRequest request = GraphQLRequest.builder().query(mutation).variables(varMap).build();
        GraphQLResponse response = graphQLWebClient.post(request).block();
        response.get("deleteCustomer", Void.class);
    }

}
