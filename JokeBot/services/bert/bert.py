from transformers import AutoTokenizer, AutoModel
import torch
import numpy as np 
from numpy.linalg import norm


tokenizer = AutoTokenizer.from_pretrained("sberbank-ai/sbert_large_nlu_ru")
model = AutoModel.from_pretrained("sberbank-ai/sbert_large_nlu_ru")


def mean_pooling(model_output, attention_mask):
    token_embeddings = model_output[0]
    input_mask_expanded = attention_mask.unsqueeze(-1).expand(token_embeddings.size()).float()
    sum_embeddings = torch.sum(token_embeddings * input_mask_expanded, 1)
    sum_mask = torch.clamp(input_mask_expanded.sum(1), min=1e-9)
    return sum_embeddings / sum_mask


def encode(actual, reference):
    sentences = [actual, reference]

    encoded_input = tokenizer(sentences, padding=True, truncation=True, max_length=24, return_tensors='pt')

    with torch.no_grad():
        model_output = model(**encoded_input)

    return mean_pooling(model_output, encoded_input['attention_mask'])


def vector_similarity(actual, reference):
    actual_vec, ref_vec = encode(actual, reference)
    denominator = norm(actual_vec) * norm(ref_vec)
    if np.isclose(denominator, 0):
        cos_sim = 0
    else:
        cos_sim = np.dot(actual_vec, ref_vec) / denominator

    return cos_sim

