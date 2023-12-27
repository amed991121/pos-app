package com.savent.erp.utils

class RepoMapper<L,N>(val local: (N)->(L), val network: (L)->(N)) {
}