#!/usr/bin/env python3

from graphviz import Digraph
import numpy as np

np.random.seed(0)


# g = Digraph('G', filename='hello.gv')

# g.edge('Hello', 'World')

# g.view()


# 3 layers, 2ish nodes per layer

def create_random_graph():
    data_nodes = []
    data_edges = []

    uid = 0
    for i in range(3): # 3 layers
        layer = []
        for j in range(np.random.randint(2, 4)): # Random nodes per layer
            layer.append((str(uid), np.random.randint(1, 9)))
            uid += 1

        data_nodes.append(layer)

    # Generate random edge weights
    for i in range(len(data_nodes)-1):
        layer_weights = []
        for j in range(len(data_nodes[i])):
            node_weights = []
            for k in range(len(data_nodes[i+1])):
                node_weights.append(np.random.randint(1, 9))
            layer_weights.append(node_weights)
        data_edges.append(layer_weights)

    return data_nodes, data_edges

def visualize_graph(data_nodes, data_edges, path):
    g = Digraph('G', filename='hello.gv')
    g.attr(rankdir='LR')

    # Add nodes to graph
    for layer_i in range(len(data_nodes)):
        for node_i in range(len(data_nodes[layer_i])):
            node = data_nodes[layer_i][node_i]
            if path[layer_i] == node_i:
                g.node(node[0], str(node[1]), color='red')
            else:
                g.node(node[0], str(node[1]))

    for layer_i in range(len(data_nodes)-1):
        for node_i in range(len(data_nodes[layer_i])):
            for other_node_i in range(len(data_nodes[layer_i+1])):
                node_uid = data_nodes[layer_i][node_i][0]
                other_node_uid = data_nodes[layer_i+1][other_node_i][0]
                edge = data_edges[layer_i][node_i][other_node_i]
                if node_i == path[layer_i] and other_node_i == path[layer_i+1]:
                    g.edge(node_uid, other_node_uid, label=str(edge), color='red')
                else:
                    g.edge(node_uid, other_node_uid, label=str(edge))


    g.view()

def calculate_minimal_path(data_nodes, data_edges):
    last_layer_costs = []  # Costs for every node in layer
    current_layer_costs = []  # Costs for every node in layer
    trace_indices = []  # trace indices back to start to find path took

    # Initialize first layer costs
    for node in data_nodes[0]:
        last_layer_costs.append(node[1])

    # Find cost of getting to next layer via each edge
    for layer_i in range(1, len(data_nodes)):
        current_layer_costs = []
        indices = []
        for node_i in range(len(data_nodes[layer_i])):
            costs = []
            for last_node_i in range(len(data_nodes[layer_i-1])):
                costs.append(last_layer_costs[last_node_i] + data_edges[layer_i-1][last_node_i][node_i])
            min_cost = min(costs)
            indices.append(costs.index(min_cost))
            current_layer_costs.append(min_cost + data_nodes[layer_i][node_i][1])

        last_layer_costs = current_layer_costs
        trace_indices.append(indices)

    min_cost = min(last_layer_costs)
    min_index = last_layer_costs.index(min_cost)
    path = [min_index]
    for layer_i in range(len(data_nodes)-2, -1, -1):
        node_i = path[0]
        path.insert(0, trace_indices[layer_i][node_i])

    return min_cost, path

# import time
# start_time = time.time()
# for i in range(int(1E4)):
#     data_nodes, data_edges = create_random_graph()
# end_time = time.time()
# print("Time elapsed: %f" % (end_time - start_time))

data_nodes, data_edges = create_random_graph()
min_cost, path = calculate_minimal_path(data_nodes, data_edges)
visualize_graph(data_nodes, data_edges, path)
print("Lowest cost path: %d" % min_cost)
